package de.rcblum.stream.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;
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
 * @version 0.1
 *
 */
public class StreamDeck implements InputReportListener {

	private class BrightnessUpdater implements Runnable {
		public BrightnessUpdater() {
		}

		@Override
		public void run() {
			StreamDeck.this._updateBrightnes();
		}
	}

	private class EventDispatcher implements Runnable {

		@Override
		public void run() {
			while (running || !running && !recievePool.isEmpty()) {
				if (!StreamDeck.this.recievePool.isEmpty()) {
					KeyEvent event = StreamDeck.this.recievePool.poll();
					int i = event.getKeyId();
					if (i < StreamDeck.this.keys.length && StreamDeck.this.keys[i] != null) {
						StreamDeck.this.keys[i].onKeyEvent(event);
					}
					StreamDeck.this.listerners.stream().forEach(l -> l.onKeyEvent(event));
				}
				if (StreamDeck.this.recievePool.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	private class DeckWorker implements Runnable {

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			long count = 0;
			while (running || !running && !sendPool.isEmpty()) {
				Runnable task = sendPool.poll();
				if (task != null) {
					try {
						task.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
//				if (count++/5 == 0)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				time = System.currentTimeMillis();
			}
		}

	}

	private class IconUpdater implements Runnable {

		int keyIndex;
		byte[] img;

		public IconUpdater(int keyIndex, byte[] img) {
			this.keyIndex = keyIndex;
			this.img = img;
		}

		@Override
		public void run() {
			StreamDeck.this._drawImage(keyIndex, img);
		}

	}

	private class Resetter implements Runnable {
		public Resetter() {
		}

		@Override
		public void run() {
			StreamDeck.this._reset();
		}
	}

	public final static byte[] RESET_DATA = new byte[] { 0x0B, 0x63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public final static byte[] BRIGHTNES_DATA = new byte[] { 0x05, 0x55, (byte) 0xAA, (byte) 0xD1, 0x01, 0x63, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static byte[] PAGE_1_HEADER = new byte[] { 0x01, 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x42, 0x4D, (byte) 0xF6, 0x3C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00,
			0x00, 0x28, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00,
			0x00, 0x00, 0x00, (byte) 0xC0, 0x3C, 0x00, 0x00, (byte) 0xC4, 0x0E, 0x00, 0x00, (byte) 0xC4, 0x0E, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static byte[] PAGE_2_HEADER = new byte[] { 0x01, 0x02, 0x00, 0x01, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	public final static int BUTTON_COUNT = 15;

	public final static int ICON_SIZE = 72;

	public final static int PAGE_PACKET_SIZE = 8190;

	public final static int NUM_FIRST_PAGE_PIXELS = 2583;

	public final static int NUM_SECOND_PAGE_PIXELS = 2601;

	public final static byte[] BLACK_ICON = createBlackIcon("temp://BLACK_ICON");

	private static byte[] createBlackIcon(String path) {
		BufferedImage img = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
		g.dispose();
		return IconHelper.cacheImage(path, img);
	}

	private HidDevice hidDevice = null;

	private byte[] brightness = BRIGHTNES_DATA;

	private StreamItem[] keys = new StreamItem[15];

	private boolean[] keysPressed = new boolean[15];

	Queue<Runnable> sendPool = new ConcurrentLinkedQueue<>();

	Queue<KeyEvent> recievePool = new ConcurrentLinkedQueue<>();

	private byte[] p1 = new byte[PAGE_PACKET_SIZE];

	private byte[] p2 = new byte[PAGE_PACKET_SIZE];

	private Thread sendWorker = null;

	private Thread recieveWorker = null;

	private boolean running = true;

	private List<StreamKeyListener> listerners;

	/**
	 * Creates a wrapper for the Stream Deck HID
	 * 
	 * @param streamDeck
	 *            Stream Decks HID Devices
	 * @param brightness
	 *            Brightness from 0 .. 99
	 */
	public StreamDeck(HidDevice streamDeck, int brightness) {
		super();
		this.hidDevice = streamDeck;
		this.hidDevice.setInputReportListener(this);
		this.brightness[5] = (byte) brightness;
		listerners = new ArrayList<>(5);
		this.sendWorker = new Thread(new DeckWorker());
		this.sendWorker.start();
		this.recieveWorker = new Thread(new EventDispatcher());
		this.recieveWorker.start();
	}

	private void _reset() {
		hidDevice.setFeatureReport(RESET_DATA, RESET_DATA.length);
	}

	private void _updateBrightnes() {
		hidDevice.setFeatureReport(this.brightness, this.brightness.length);
	}

	public void addKey(int keyId, StreamItem item) {
		if (keyId < this.keys.length && keyId >= 0) {
			this.keys[keyId] = item;
			sendPool.add(new IconUpdater(keyId, item.getIcon()));
		}
	}

	public boolean addKeyListener(StreamKeyListener listener) {
		return this.listerners.add(listener);
	}

	public boolean remvoeKeyListener(StreamKeyListener listener) {
		return this.listerners.remove(listener);
	}

	public void drawImage(int keyIndex, byte[] imgData) {
		sendPool.add(new IconUpdater(keyIndex, imgData));
	}

	private void _drawImage(int keyIndex, byte[] imgData) {
		// int[] pixels = ((DataBufferInt)
		// img.getRaster().getDataBuffer()).getData();
		// byte[] imgData = new byte[ICON_SIZE * ICON_SIZE * 3];
		// int imgDataCount=0;
		// // remove the alpha channel
		// for(int i=0;i<ICON_SIZE*ICON_SIZE; i++) {
		// //RGB -> BGR
		// imgData[imgDataCount++] = (byte)((pixels[i]>>16) & 0xFF);
		// imgData[imgDataCount++] = (byte)(pixels[i] & 0xFF);
		// imgData[imgDataCount++] = (byte)((pixels[i]>>8) & 0xFF);
		// }
		byte[] page1 = generatePage1(keyIndex, imgData);
		byte[] page2 = generatePage2(keyIndex, imgData);
		this.hidDevice.setOutputReport((byte) 0x02, page1, page1.length);
		this.hidDevice.setOutputReport((byte) 0x02, page2, page2.length);
	}

	private void fireKeyChangedEvent(int i, boolean keyPressed) {
		KeyEvent event = new KeyEvent(this, i, keyPressed ? Type.PRESSED : Type.RELEASED_CLICKED);
		this.recievePool.add(event);
//		if (i < this.keys.length && this.keys[i] != null) {
//			this.keys[i].onKeyEvent(event);
//		}
//		this.listerners.stream().forEach(l -> l.onKeyEvent(event));
	}

	/**
	 * Generates HID-Report Page 1/2 to update an image of one stream deck key
	 * 
	 * @param keyId
	 *            Id of the key to be updated
	 * @param imgData
	 *            image data in the bgr-format
	 * @return HID-Report in byte format ready to be send to the stream deck
	 */
	private byte[] generatePage1(int keyId, byte[] imgData) {
		for (int i = 0; i < PAGE_1_HEADER.length; i++) {
			p1[i] = PAGE_1_HEADER[i];
		}
		if (imgData != null) {
			// byte[] imgDataPage1 = Arrays.copyOf(imgData,
			// NUM_FIRST_PAGE_PIXELS * 3);
			for (int i = 0; i < imgData.length && i < NUM_FIRST_PAGE_PIXELS * 3; i++) {
				p1[PAGE_1_HEADER.length + i] = imgData[i];
			}
		}
		p1[4] = (byte) (keyId + 1);
		return p1;
	}

	/**
	 * Generates HID-Report Page 2/2 to update an image of one stream deck key
	 * 
	 * @param keyId
	 *            Id of the key to be updated
	 * @param imgData
	 *            image data in the bgr-format
	 * @return HID-Report in byte format ready to be send to the stream deck
	 */
	private byte[] generatePage2(int keyId, byte[] imgData) {
		for (int i = 0; i < PAGE_2_HEADER.length; i++) {
			p2[i] = PAGE_2_HEADER[i];
		}
		if (imgData != null) {
			// byte[] imgDataPage2 = Arrays.copyOfRange(imgData,
			// NUM_FIRST_PAGE_PIXELS * 3, (NUM_FIRST_PAGE_PIXELS * 3) +
			// (NUM_SECOND_PAGE_PIXELS * 3));
			// for (int i = 0; i < imgDataPage2.length; i++) {
			for (int i = 0; i < NUM_SECOND_PAGE_PIXELS * 3 && i < imgData.length; i++) {
				p2[PAGE_2_HEADER.length + i] = imgData[(NUM_FIRST_PAGE_PIXELS * 3) + i];
			}
		}
		p2[4] = (byte) (keyId + 1);
		return p2;
	}

	/**
	 * Returns the Hid Devices representation the stream deck.
	 * 
	 * @return HidDevice representation the stream deck.
	 */
	public HidDevice getHidDevice() {
		return hidDevice;
	}

	@Override
	public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
		if (reportID == 1) {
			for (int i = 0; i < 15 && i < reportLength; i++) {
				if (keysPressed[i] != (reportData[i] == 0x01)) {
					fireKeyChangedEvent(i, reportData[i] == 0x01);
					keysPressed[i] = reportData[i] == 0x01;
				}
			}
		}
	}

	/**
	 * Removes a registered Key. Queues update to the stream deck
	 * 
	 * @param keyId
	 *            id of the key to be removed
	 */
	public void removeKey(int keyId) {
		if (keyId < this.keys.length && keyId >= 0 && this.keys[keyId] != null) {
			this.keys[keyId] = null;
			sendPool.add(new IconUpdater(keyId, BLACK_ICON));
		}
	}

	/**
	 * Queues a task to reset the stream deck.
	 */
	public void reset() {
		this.sendPool.add(new Resetter());
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null)
				this.sendPool.add(new IconUpdater(i, keys[i].getIcon()));
		}
	}

	/**
	 * Sets the desired brightness from 0 - 100 % and queues the change.
	 * 
	 * @param brightness
	 */
	public void setBrightness(int brightness) {
		brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
		this.brightness[5] = (byte) brightness;
		this.sendPool.add(new BrightnessUpdater());
	}

	/**
	 * Tells the background task for the stream deck to stop working.
	 */
	public void stop() {
		this.running = false;
	}

	/**
	 * Wait for all tasks to be executed
	 */
	public void waitForCompletion() {
		long time = 0;
		while (this.sendPool.size() != 0) {
			if (this.sendPool.size() > 500)
				System.out.println("Send pool: " + this.sendPool.size());
			try {
				Thread.sleep(50);
				time += 50;
				if (time > 20_000)
					return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void clearButton(int i) {
		sendPool.add(new IconUpdater(i, BLACK_ICON));
	}
}
