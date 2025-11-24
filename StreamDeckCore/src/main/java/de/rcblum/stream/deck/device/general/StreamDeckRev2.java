package de.rcblum.stream.deck.device.general;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

/**
 * Provides low level access to a connected stream deck. Allows to do the
 * following without having to deal with the HID format:<br>
 * 1. Reset the device<br>
 * 2. Set the brightness of the device<br>
 * 3. Set an image for one of the key at a time.<br>
 * 4. Receive an {@link KeyEvent} whenever a key was
 * pressed/released/clicked.<br>
 * <br>
 * <br>
 * For the device to properly function, it has to be initialised properly. This
 * is done by 1.) resetting it, 2) setting the brightness of the device. <br>
 * <br>
 * All processing is don asynchronous, meaning anything sent to the deck is put
 * in a queue and processed in another thread. Processing is done at a tick rate
 * of 100 (100 updates per second max).
 * 
 * <br>
 * <br>
 * 
 * MIT License<br>
 * <br>
 * Copyright (c) 2017 Roland von Werden<br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy<br>
 * of this software and associated documentation files (the "Software"), to
 * deal<br>
 * in the Software without restriction, including without limitation the
 * rights<br>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>
 * copies of the Software, and to permit persons to whom the Software is<br>
 * furnished to do so, subject to the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be included
 * in<br>
 * all copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR<br>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE<br>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,<br>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE<br>
 * SOFTWARE.<br>
 * 
 * @author Roland von Werden
 * @version 1.0.0
 *
 */
public class StreamDeckRev2 implements InputReportListener, IStreamDeck {
	
	private static final Logger LOGGER = LogManager.getLogger(StreamDeckRev2.class);

	/**
	 * Job that is submitted, when the Method {@link StreamDeckRev2#setBrightness(int)} is called.<br>
	 * When executed it will call the Method {@link StreamDeckRev2#internalUpdateBrightnes()}, which will send the brightness command to the stream deck.
	 * @author Roland von Werden
	 *
	 */
	private class BrightnessUpdater implements Runnable {
		public BrightnessUpdater() {
		}

		@Override
		public void run() {
			StreamDeckRev2.this.internalUpdateBrightnes();
		}
	}

	/**
	 * Dispatcher that asynchronously sends out all issued {@link KeyEvent}s.
	 * @author Roland von Werden
	 *
	 */
	private class EventDispatcher implements Runnable {

		@Override
		public void run() {
			while (StreamDeckRev2.this.running || !StreamDeckRev2.this.running && !recievePool.isEmpty()) {
				if (!StreamDeckRev2.this.recievePool.isEmpty()) {
					KeyEvent event = StreamDeckRev2.this.recievePool.poll();
					int i = event.getKeyId();
					if (i < StreamDeckRev2.this.keys.length && StreamDeckRev2.this.keys[i] != null) {
						StreamDeckRev2.this.keys[i].onKeyEvent(event);
					}
					StreamDeckRev2.this.listerners.stream().forEach(l -> 
						{
							try {
								l.onKeyEvent(event);
							} 
							catch (Exception e) {
								LOGGER.error("Error sending out KeyEvents", e);
							}
						}
					);
				}
				if (StreamDeckRev2.this.recievePool.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						LOGGER.error("EventDispatcher sleep interrupted", e);
						Thread.currentThread().interrupt();
					}
				}
			}
		}

	}

	/**
	 * Dispatches all commands asynchronously queued up in {@link StreamDeckRev2#sendPool} to the ESD.
	 * Send rate is limited to 500 commands per second.
	 * If the execution of one command is completed in less tha on ms the thread is put to sleep for 1 ms.
	 * As long as one loop takes up less then 2 ms the rest of the time is actively wated
	 * 
	 * @author Roland von Werden
	 *
	 */
	private class DeckWorker implements Runnable {
		
		@SuppressWarnings("unused")
		@Override
		public void run() {
			long actions = 0;
			long time = System.currentTimeMillis();
			long t = 0;
			while (running || !running && !sendPool.isEmpty()) {
				if(sendPool.size() > 100) {
					Runnable[] payloads = new Runnable[StreamDeckRev2.this.getKeySize()];
					Runnable resetTask = null;
					Runnable brightnessTask = null;
					Runnable task = null;
					while ((task = sendPool.poll()) != null) {
						if (task instanceof IconUpdater) {
							IconUpdater iu = (IconUpdater)task;
							payloads[iu.keyIndex] = iu;
						}
						else if(task instanceof Resetter)
							resetTask = task;
						else if (task instanceof BrightnessUpdater) 
							brightnessTask = task;
					}
					if(brightnessTask != null)
						sendPool.add(brightnessTask);
					if(resetTask != null)
						sendPool.add(resetTask);
					for (int i = 0; i < payloads.length; i++) {
						if(payloads[i] != null)
							sendPool.add(payloads[i]);
					}
				}
				t = System.nanoTime();
				Runnable task = sendPool.poll();
				if (task != null) {
					try {
						task.run();
					} catch (Exception e) {
						LOGGER.error("Error sending the following command-class th the esd: " + task.getClass() );
						LOGGER.error(e);
					}
				}
				if (System.nanoTime()-t < 1_000)
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						LOGGER.error("DeckWorker interrupted", e);
						Thread.currentThread().interrupt();
					}
				if(LOGGER.isDebugEnabled()) {
					actions++;
					if(System.currentTimeMillis() - time > 30_000) {
						LOGGER.debug("Commands send per one second: " + (actions/10));
						LOGGER.debug("Commands send per 30 seconds: " + actions);
						time = System.currentTimeMillis();
						actions = 0;
					}
				}
				while(System.nanoTime()-t < 2_000) {
					Math.subtractExact(2, 1);
				}
			}
		}

	}

	/**
	 * Sends an Icon to a given key on the ESD.
	 * @author Roland von Werden
	 *
	 */
	private class IconUpdater implements Runnable {

		int keyIndex;
		SDImage img;

		public IconUpdater(int keyIndex, SDImage img) {
			this.keyIndex = keyIndex;
			this.img = img;
		}

		@Override
		public void run() {
			StreamDeckRev2.this.internalDrawImage(keyIndex, img);
		}

	}

	/**
	 * Sends the reset command to the ESD.
	 * @author Roland von Werden
	 *
	 */
	private class Resetter implements Runnable {
		public Resetter() {}

		@Override
		public void run() {
			StreamDeckRev2.this.internalReset();
		}
	}


	/**
     * Reset command
     */
    private static final byte[] RESET_DATA = new byte[]{
    		0x03, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /**
     * Brightness command
     */
    private static final byte[] BRIGHTNES_DATA = new byte[]{
            0x03, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    };

    /**
     * Header for all pages of the image command
     */
    private static final byte[] IMAGE_PAGE_HEADER = new byte[]{
    		0x02, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

	/**
	 * Number of buttons on the ESD, assuming the standard ESD
	 */
	public static final int BUTTON_COUNT = 15;
	
	/**
	 * Number of rows on the ESD, assuming the standard ESD
	 */
	public static final int ROW_COUNT = 3;

	/**
	 * Icon size of one key
	 */
	public static final int ICON_SIZE = 72;

    /**
     * Page size that can be sent to the ESD at once
     */
    public static final int PAGE_PACKET_SIZE = 1024;

	/**
	 * Back image for not used keys
	 */
	public static final SDImage BLACK_ICON = createBlackIcon("temp://BLACK_ICON");

	private static SDImage createBlackIcon(String path) {
		BufferedImage img = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
		g.dispose();
		return IconHelper.cacheImage(path, img);
	}

	/**
	 * HidDevice associated with the connected ESD
	 */
	private HidDevice hidDevice = null;

	/**
	 * Brightness command for this instance.
	 */
	private byte[] brightness = Arrays.copyOf(BRIGHTNES_DATA, BRIGHTNES_DATA.length);

	/**
	 * Keys set to be displayed on the StreamDeck
	 */
	private StreamItem[] keys = new StreamItem[StreamDeckRev2.this.getKeySize()];

	/**
	 * current values if a key on a certain index is pressed or not
	 */
	private boolean[] keysPressed = new boolean[StreamDeckRev2.this.getKeySize()];

	/**
	 * Amount of keys present in the stream deck.
	 */
	private int keyCount =  BUTTON_COUNT;
	/**
	 * Amount of rows present in the stream deck.
	 */
	private int rowCount =  ROW_COUNT;

	/**
	 * Queue for commands to be sent to the ESD
	 */
	private Queue<Runnable> sendPool = new ConcurrentLinkedQueue<>();

	/**
	 * Queue for {@link KeyEvent}s that are triggered by the ESD
	 */
	private Queue<KeyEvent> recievePool = new ConcurrentLinkedQueue<>();

	/**
	 * Page 1 for the image command
	 */
	private byte[] p1 = new byte[PAGE_PACKET_SIZE];

	/**
	 * Page 2 for the image command
	 */
	private byte[] p2 = new byte[PAGE_PACKET_SIZE];

	/**
	 * Daemon that send commands to the ESD
	 */
	private Thread sendWorker = null;

	/**
	 * Daemon that sends received {@link KeyEvent}s  to the affected listeners.
	 */
	private Thread eventDispatcher = null;

	/**
	 * Intended state of the daemons
	 */
	private boolean running = true;

	/**
	 * Registered Listeners to the {@link KeyEvent}s created by the ESD
	 */
	private List<StreamKeyListener> listerners;

	/**
	 * Creates a wrapper for the Stream Deck HID
	 * 
	 * @param streamDeck
	 *            Stream Decks HID Devices
	 * @param brightness
	 *            Brightness from 0 .. 99
	 */
	public StreamDeckRev2(HidDevice streamDeck, int brightness, int keyCount, int rowCount) {
		super();
		this.keyCount = keyCount;
		this.rowCount = rowCount;
		this.keys = new StreamItem[this.getKeySize()];
		this.keysPressed = new boolean[this.getKeySize()];
		this.hidDevice = streamDeck;
		this.hidDevice.setInputReportListener(this);
		this.brightness[2] = (byte) brightness;
		listerners = new ArrayList<>(5);
		this.sendWorker = new Thread(new DeckWorker());
		this.sendWorker.setDaemon(true);
		this.sendWorker.start();
		this.eventDispatcher = new Thread(new EventDispatcher());
		this.eventDispatcher.setDaemon(true);
		this.eventDispatcher.start();
	}

	/**
	 * Sends reset-command to ESD
	 */
	private void internalReset() {
		hidDevice.setFeatureReport(RESET_DATA[0], Arrays.copyOfRange(RESET_DATA, 1, RESET_DATA.length), RESET_DATA.length-1);
	}


	/**
	 * Sends brightness-command to ESD
	 */
	private void internalUpdateBrightnes() {
		hidDevice.setFeatureReport(this.brightness[0], Arrays.copyOfRange(this.brightness, 1, this.brightness.length), this.brightness.length-1);
	}
	
	@Override
	public int getKeySize() {
		return this.keyCount;
	}
	
	@Override
	public int getRowSize() {
		return this.rowCount;
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#addKey(int, de.rcblum.stream.deck.items.StreamItem)
	 */
	@Override
	public void addKey(int keyId, StreamItem item) {
		if (keyId < this.keys.length && keyId >= 0) {
			this.keys[keyId] = item;
			queue(new IconUpdater(keyId, item.getIcon()));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#addKeyListener(de.rcblum.stream.deck.event.StreamKeyListener)
	 */
	@Override
	public boolean addKeyListener(StreamKeyListener listener) {
		return this.listerners.add(listener);
	}


	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#remvoeKeyListener(de.rcblum.stream.deck.event.StreamKeyListener)
	 */
	@Override
	public boolean removeKeyListener(StreamKeyListener listener) {
		return this.listerners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#drawImage(int, de.rcblum.stream.deck.util.SDImage)
	 */
	@Override
	public void drawImage(int keyIndex, SDImage imgData) {
		queue(new IconUpdater(keyIndex, imgData));
	}

   public synchronized void internalDrawImage(int keyIndex, SDImage imgData) {
        int reportLength = 1024;
        int pageLength = reportLength - IMAGE_PAGE_HEADER.length;
        int pages = (int) Math.ceil(((float) imgData.sdImageJpeg.length) / pageLength);
        int totalBytes = imgData.sdImageJpeg.length;
        // Send Image in split reports
        for (int pageNo = 0; pageNo < pages; pageNo++) {
            byte[] page = Arrays.copyOfRange(imgData.sdImageJpeg, pageNo * pageLength, Math.min(((pageNo + 1) * pageLength), imgData.sdImageJpeg.length));
            totalBytes -= totalBytes;
            byte[] report = new byte[reportLength];
            for (int i1 = 0; i1 < IMAGE_PAGE_HEADER.length; i1++) {
                report[i1] = IMAGE_PAGE_HEADER[i1];
            }
            for (int j = 0; j < page.length; j++) {
                report[IMAGE_PAGE_HEADER.length + j] = page[j];
            }
            // Key to be updated
            report[2] = (byte) keyIndex;
            // 0 = More pages are beeing sent, 1 = this is the last page of the image
            report[3] = pageNo < pages - 1 ? (byte) 0x00 : (byte) 0x01;
            // Length of the payload sent
            report[4] = (byte) (page.length & 0xff);
            report[5] = (byte) ((page.length >> 8) & 0xff);
            // Number of the page sent
            report[6] = (byte) (pageNo & 0xff);
            report[7] = (byte) ((pageNo >> 8) & 0xff);

            int result = this.hidDevice.setOutputReport((byte) report[0], Arrays.copyOfRange(report, 1, report.length), report.length - 1);
            System.out.println("key " + keyIndex + ", frame " + pageNo + " => " + result);
            if (result < 0) {
                break;
            }
        }
        System.out.println("total bytes: " + totalBytes);
    }

	private void fireKeyChangedEvent(int i, boolean keyPressed) {
		KeyEvent event = new KeyEvent(this, i, keyPressed ? Type.PRESSED : Type.RELEASED_CLICKED);
		this.recievePool.add(event);
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#getHidDevice()
	 */
	@Override
	public HidDevice getHidDevice() {
		return hidDevice;
	}

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        int byteOffset = 3;
        if (reportID == 1) {
            for (int i = byteOffset; i < StreamDeckRev2.this.getKeySize() + byteOffset && i < reportLength; i++) {
                int keyIndex = i - byteOffset;
                if (keysPressed[keyIndex] != (reportData[i] == 0x01)) {
                    fireKeyChangedEvent(keyIndex, reportData[i] == 0x01);
                    keysPressed[keyIndex] = reportData[i] == 0x01;
                }
            }
        }
    }

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#removeKey(int)
	 */
	@Override
	public void removeKey(int keyId) {
		if (keyId < this.keys.length && keyId >= 0 && this.keys[keyId] != null) {
			this.keys[keyId] = null;
			queue(new IconUpdater(keyId, BLACK_ICON));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#reset()
	 */
	@Override
	public void reset() {
		this.queue(new Resetter());
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null)
				this.queue(new IconUpdater(i, keys[i].getIcon()));
			else
				this.queue(new IconUpdater(i, BLACK_ICON));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#setBrightness(int)
	 */
	@Override
	public void setBrightness(int brightness) {
		brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
		this.brightness[2] = (byte) brightness;
		this.queue(new BrightnessUpdater());
	}

	private void queue(Runnable payload) {
		if(this.running)
			this.sendPool.add(payload);
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#stop()
	 */
	@Override
	public void stop() {
		this.running = false;
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#waitForCompletion()
	 */
	@Override
	public void waitForCompletion() {
		long time = 0;
		while (this.sendPool.isEmpty()) {
			try {
				Thread.sleep(50);
				time += 50;
				if (time > 2_000)
					return;
			} catch (InterruptedException e) {

				LOGGER.error("StreamDeck was interrupted", e);
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * @see IStreamDeck#clearButton(int)
	 */
	@Override
	public void clearButton(int i) {
		queue(new IconUpdater(i, BLACK_ICON));
	}

	public StreamItem[] getItems() {
		return this.keys;
	}
	
	@Override
	public boolean isHardware() {
		return true;
	}

	/**
	 * Manually Pushing a button at the given id.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void pushButton(int no) {
		LOGGER.debug(String.format("Virtual button pushed: Key-ID: %d", no));
		no = no > 14 ? 14 : no < 0 ? 0 : no;
		KeyEvent evnt = new KeyEvent(this, no, Type.RELEASED_CLICKED);
		recievePool.add(evnt);
	}

	/**
	 * Manually presses a button at the given id until {@link #releaseButton(int)}
	 * is called.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void pressButton(int no) {
		LOGGER.debug(String.format("Virtual button pressed: Key-ID: %d", no));
		no = no > 14 ? 14 : no < 0 ? 0 : no;
		KeyEvent evnt = new KeyEvent(this, no, Type.PRESSED);
		recievePool.add(evnt);
	}

	/**
	 * Manually releases a button at the given id. If the button is not pressed, it
	 * will be pushed instead.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void releaseButton(int no) {
		LOGGER.debug(String.format("Virtual button released: Key-ID: %d", no));
		this.pushButton(no);
	}
}
