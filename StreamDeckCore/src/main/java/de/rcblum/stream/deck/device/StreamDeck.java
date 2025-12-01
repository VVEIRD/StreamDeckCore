package de.rcblum.stream.deck.device;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.descriptor.DeckDescriptor;
import de.rcblum.stream.deck.device.descriptor.KeyType;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawImageInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawTouchscreenInterface;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.device.worker.DeckWorker;
import de.rcblum.stream.deck.device.worker.EventDispatcher;
import de.rcblum.stream.deck.device.worker.DeckUpdater;
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
public class StreamDeck implements InputReportListener, IStreamDeck {
	
	private static final Logger LOGGER = LogManager.getLogger(StreamDeck.class);
	
	/**
	 * Descriptor for the deck device
	 */
	private DeckDescriptor descriptor = null;
	
	/**
	 * HidDevice associated with the connected ESD
	 */
	private HidDevice hidDevice = null;

	/**
	 * Brightness command for this instance.
	 */
	private int brightness = 70;

	/**
	 * Keys set to be displayed on the StreamDeck
	 */
	private StreamItem[] keys = new StreamItem[15];

	/**
	 * current values if a key on a certain index is pressed or not
	 */
	private boolean[] keysPressed = new boolean[15];

	/**
	 * current values if one of the special keys on a certain index is pressed or not
	 */
	private boolean[] specialKeysPressed = new boolean[15];

	/**
	 * Queue for commands to be sent to the ESD
	 */
	private Queue<DeckUpdater> sendPool = new ConcurrentLinkedQueue<>();

	/**
	 * Queue for {@link KeyEvent}s that are triggered by the ESD
	 */
	private Queue<KeyEvent> recievePool = new ConcurrentLinkedQueue<>();

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
	public StreamDeck(DeckDescriptor descriptor, HidDevice streamDeck, int brightness) {
		super();
		this.descriptor = descriptor;
		this.keys = new StreamItem[this.getKeySize()];
		this.keysPressed = new boolean[descriptor.getKeySize()];
		this.specialKeysPressed = new boolean[descriptor.getSpecialKeySize()];
		this.hidDevice = streamDeck;
		this.hidDevice.setInputReportListener(this);
		this.brightness = brightness;
		listerners = new CopyOnWriteArrayList<>();
		this.sendWorker = new Thread(new DeckWorker(this));
		this.sendWorker.setDaemon(true);
		this.sendWorker.start();
		this.eventDispatcher = new Thread(new EventDispatcher(this));
		this.eventDispatcher.setDaemon(true);
		this.eventDispatcher.start();
	}
	
	@Override
	public int getKeySize() {
		return this.descriptor.getKeySize();
	}
	
	@Override
	public int getRowSize() {
		return this.descriptor.rows;
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#addKey(int, de.rcblum.stream.deck.items.StreamItem)
	 */
	@Override
	public void addKey(int keyId, StreamItem item) {
		if (keyId < this.keys.length && keyId >= 0) {
			this.keys[keyId] = item;
			queue(new DeckUpdater(this.hidDevice, this.descriptor.drawImageInterface, keyId + this.getDescriptor().drawImageKeyOffset, item.getIcon(), this.descriptor.iconSize));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#addKeyListener(de.rcblum.stream.deck.event.StreamKeyListener)
	 */
	@Override
	public boolean addKeyListener(StreamKeyListener listener) {
		if (this.listerners.contains(listener))
			return false;
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
		drawImage(keyIndex, imgData, this.getDescriptor().getKey(keyIndex).getDimension());
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#drawImage(int, de.rcblum.stream.deck.util.SDImage)
	 */
	@Override
	public void drawImage(int keyIndex, SDImage imgData, Dimension overrideSize) {
		DrawImageInterface dI = this.descriptor.drawImageInterface;
		DrawTouchscreenInterface dT = this.descriptor.drawTouchScreenInterface;
		
		if (this.getDescriptor().getKey(keyIndex) != null && this.getDescriptor().getKey(keyIndex).equals(KeyType.TOUCH_SCREEN))  {
			queue(new DeckUpdater(this.hidDevice, dT, new Point(0, 0), imgData, overrideSize));
		}
		else {
			queue(new DeckUpdater(this.hidDevice, dI, keyIndex + this.getDescriptor().drawImageKeyOffset, imgData, overrideSize));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#drawImage(int, de.rcblum.stream.deck.util.SDImage)
	 */
	@Override
	public void drawFullImage(SDImage imgData) {
		queue(new DeckUpdater(this.hidDevice, this.descriptor.drawFullImageInterface, 0, imgData, this.descriptor.fullDisplaySize));
	}
	
	@Override
	public boolean hasTouchScreen() {
		return this.descriptor.touchScreenIndex > 0;
	}
	
	@Override
	public void drawTouchScreenImage(SDImage imgData) {
		drawTouchScreenImage(new Point(0,0), imgData);
	}
	
	@Override
	public void drawTouchScreenImage(Point startPoint, SDImage imgData) {
		queue(new DeckUpdater(this.hidDevice, this.descriptor.drawTouchScreenInterface, startPoint, imgData, imgData.imageSize));
	}
   
   public synchronized boolean sendOutputReport(byte[] report) {
	   int result = this.hidDevice.setOutputReport(report[0], Arrays.copyOfRange(report, 1, report.length), report.length - 1);
	   return result >= 0;
   }

	private void fireKeyChangedEvent(int i, boolean keyPressed) {
		KeyEvent event = new KeyEvent(this, i, keyPressed ? Type.PRESSED : Type.RELEASED_CLICKED);
		this.recievePool.add(event);
	}

	private void fireKeyEvent(KeyEvent event) {
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
        int byteOffset = StreamDeck.this.descriptor.keyEventInputReportOffset;
        LOGGER.debug("");
        LOGGER.debug(String.format("Type: 0x%02X 0x%02X 0x%02X", Byte.valueOf(reportData[0]), Byte.valueOf(reportData[1]), Byte.valueOf(reportData[2])));
        //LOGGER.debug(String.format("Type: %4s %4s %4s", Byte.valueOf(reportData[0]), Byte.valueOf(reportData[1]), Byte.valueOf(reportData[2])));
        //LOGGER.debug(String.format("Legnth: %4s", reportLength));
        int lastNonZero = 0;
        String data1 = "Data:";
        String data2 = "     ";
        String data3 = "     ";
        String data4 = "     ";
        String formatString = " 0x%02X";
        //formatString = " %4s";
        for (int i = 0; i < reportData.length; i++) {
			if (reportData[i] != 0)
				lastNonZero = i;
			if (i < 8) {
				data1 += String.format(formatString, reportData[i]);
			}
			else if (i < 16) {
				data2 += String.format(formatString, reportData[i]);
			}
			else if (i < 24) {
				data3 += String.format(formatString,  reportData[i]);
			}
			else if (i < 32) {
				data4 += String.format(formatString, reportData[i]);
			}
		}
        LOGGER.debug(data1);
        LOGGER.debug(data2);
        //LOGGER.debug(String.format("Reverse  [4, 5][6, 7]: %4s %4s", value1rev, value2rev));
        //LOGGER.debug(data3);
        //LOGGER.debug(data4);
    	// Check what report was sent
    	int reportType = StreamDeck.getInputReportType(this.descriptor, reportData);
        if (reportID == 1 && reportType == 1) {
            processKeyEvents(reportData, reportLength, byteOffset);
        }
        else if (reportID == 1 && reportType == 2) {
            processTouchEvents(reportData, reportLength, byteOffset);
        }
        else if (reportID == 1 && reportType == 3) {
            processDialEvents(reportData, reportLength, byteOffset);
        }
    }

	private void processDialEvents(byte[] reportData, int reportLength, int byteOffset) {
		boolean isRotation = reportData[byteOffset] == 1;
		byteOffset += 1;
		int keyReportOffset = descriptor.getSpecialKeyOffset();
		int firstDialKeyIndex = 0;
		KeyType[] sKeys = descriptor.getSpecialKeys();
		// Determine the first Dial Key
		for (int i = 0; i < sKeys.length; i++) {
			if (sKeys[i].equals(KeyType.ROTARY_ENCODER)) {
				firstDialKeyIndex = i;
				break;
			}
		}
		// Generate events
		for (int i = byteOffset; i < + descriptor.getSpecialKeySize() - firstDialKeyIndex + byteOffset && i < reportLength; i++) {
			int keyIndex = i - byteOffset + firstDialKeyIndex;
			if (!isRotation && specialKeysPressed[keyIndex] != (reportData[i] == 0x01)) {
		        fireKeyChangedEvent(keyIndex+descriptor.getSpecialKeyOffset(), reportData[i] == 0x01);
		        specialKeysPressed[keyIndex] = reportData[i] == 0x01;
		    }
			else if (isRotation && reportData[i] != 0x00){
		        fireKeyEvent(new KeyEvent(this, keyIndex+descriptor.getSpecialKeyOffset(), reportData[i] < 0 ? Type.ROTATE_LEFT : Type.ROTATE_RIGHT, 0, Math.abs(reportData[i])));
		    }
		}
	}

	private void processTouchEvents(byte[] reportData, int reportLength, int byteOffset) {
		boolean isSimpleTouch = reportData[byteOffset] == 0x01;
		boolean isLongTouch   = reportData[byteOffset] == 0x02;
		boolean isGesture     = reportData[byteOffset] == 0x03;

        int startX = ((reportData[6] & 0xFF) << 8) | (reportData[5] & 0xFF);
        int startY = (reportData[7] & 0xFF);
        
        int endX = ((reportData[10] & 0xFF) << 8) | (reportData[9] & 0xFF);
        int endY = (reportData[11] & 0xFF);

		int keyReportOffset = descriptor.getSpecialKeyOffset();
		KeyType[] sKeys = descriptor.getSpecialKeys();
		// Determine the touch screen index
		for (int i = 0; i < sKeys.length; i++) {
			if (sKeys[i].equals(KeyType.TOUCH_SCREEN)) {
				keyReportOffset += i;
				break;
			}
		}
        if(isSimpleTouch) {
        	fireKeyEvent(new KeyEvent( this, keyReportOffset, Type.TOUCHED, null, new Point(startX, startY)));
        }
        else if(isLongTouch) {
        	fireKeyEvent(new KeyEvent( this, keyReportOffset, Type.TOUCHED_LONG, null, new Point(startX, startY)));
        }
        else if(isGesture) {
	        Type type = Type.SWIPED;
	        if (startX <= 30 && endX > startX && Math.abs(startX - endX) > Math.abs(startY - endY))
	        	type = Type.SWIPE_LEFT;
	        else if (startX >= 770 && endX < startX && Math.abs(startX - endX) > Math.abs(startY - endY))
	        	type = Type.SWIPE_RIGHT;
	        if (startX <= 30 && endX > startX && Math.abs(startX - endX) > Math.abs(startY - endY))
	        	type = Type.SWIPE_LEFT;
	        else if (endY < startY && Math.abs(startX - endX) < Math.abs(startY - endY))
	        	type = Type.SWIPE_UP;
	        else if (endY > startY && Math.abs(startX - endX) < Math.abs(startY - endY))
	        	type = Type.SWIPE_DOWN;
        	fireKeyEvent(new KeyEvent( this, keyReportOffset, type, new Point(startX, startY), new Point(endX, endY)));
        }
		
	}

	private void processKeyEvents(byte[] reportData, int reportLength, int byteOffset) {
		for (int i = byteOffset; i < StreamDeck.this.getKeySize() + byteOffset && i < reportLength; i++) {
		    int keyIndex = i - byteOffset;
		    if (keysPressed[keyIndex] != (reportData[i] == 0x01)) {
		        fireKeyChangedEvent(keyIndex, reportData[i] == 0x01);
		        keysPressed[keyIndex] = reportData[i] == 0x01;
		    }
		}
	}
    
    private static int getInputReportType(DeckDescriptor d, byte[] reportData) {
    	int type = 1;
    	if (Arrays.mismatch(
	    			d.inputReportKeys, 0, d.inputReportKeys.length, 
	    			reportData,        0, d.inputReportKeys.length
	    	) == -1)
    		type = 1;
    	else if (d.inputReportTouchScreen != null && Arrays.mismatch(
	    			d.inputReportTouchScreen, 0, d.inputReportTouchScreen.length, 
	    			reportData,        0, d.inputReportTouchScreen.length
	    	) == -1)
			type = 2;
    	else if (d.inputReportDials != null && Arrays.mismatch(
	    			d.inputReportDials, 0, d.inputReportDials.length, 
	    			reportData,        0, d.inputReportDials.length
	    	) == -1)
			type = 3;
    	return type;
    }

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#removeKey(int)
	 */
	@Override
	public void removeKey(int keyId) {
		if (keyId < this.keys.length && keyId >= 0 && this.keys[keyId] != null) {
			this.keys[keyId] = null;
			queue(new DeckUpdater(this.hidDevice, this.descriptor.drawImageInterface, keyId, StreamDeckConstants.BLACK_ICON, this.descriptor.iconSize));
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#reset()
	 */
	@Override
	public void reset() {
		this.queue(new DeckUpdater(this.hidDevice, this.descriptor.resetInterface));
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null)
				this.queue(new DeckUpdater(this.hidDevice, this.descriptor.drawImageInterface, i + this.getDescriptor().drawImageKeyOffset, keys[i].getIcon(), this.descriptor.iconSize));
			else
				this.queue(new DeckUpdater(this.hidDevice, this.descriptor.drawImageInterface, i + this.getDescriptor().drawImageKeyOffset, StreamDeckConstants.BLACK_ICON, this.descriptor.iconSize));
		}
		if(this.hasTouchScreen()) {
			this.drawTouchScreenImage(IconHelper.BLACK_TOUCH_SCREEN);
		}
	}

	/* (non-Javadoc)
	 * @see de.rcblum.stream.deck.IStreamDeck#setBrightness(int)
	 */
	@Override
	public void setBrightness(int brightness) {
		brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
		this.brightness = brightness;
		this.queue(new DeckUpdater(this.hidDevice, this.descriptor.brightnessInterface, this.brightness));
	}

	private void queue(DeckUpdater payload) {
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
		queue(new DeckUpdater(this.hidDevice, this.descriptor.drawImageInterface, i + this.getDescriptor().drawImageKeyOffset, StreamDeckConstants.BLACK_ICON, this.descriptor.iconSize));
	}

	public StreamItem[] getItems() {
		return this.keys;
	}
	
	@Override
	public boolean isHardware() {
		return true;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public DeckDescriptor getDescriptor() {
		return this.descriptor;
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

	public boolean isRecievePoolEmpty() {
		return this.recievePool.isEmpty();
	}

	public StreamItem getKey(int i) {
		return i >= 0 && i < this.keys.length ? this.keys[i] : null;
	}

	public KeyEvent pollRecievePool() {
		return this.recievePool.poll();
	}

	public List<StreamKeyListener> getListeners() {
		return this.listerners;
	}

	public boolean isSendPoolEmpty() {
		return this.sendPool.isEmpty();
	}

	public int getSendPoolSize() {
		return this.sendPool.size();
	}

	public DeckUpdater pollSendPool() {
		return this.sendPool.poll();
	}

	public void addToSendPool(DeckUpdater task) {
		this.sendPool.add(task);
	}
}
