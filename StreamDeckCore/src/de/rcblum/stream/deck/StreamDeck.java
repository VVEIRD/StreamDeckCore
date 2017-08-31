package de.rcblum.stream.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

/**
 * 
 * 
 * MIT License
 * 
 * Copyright (c) 2017 Roland von Werden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author Roland von Werden
 * @version 0.1
 *
 */
public class StreamDeck implements InputReportListener{
	
	public final static byte[] RESET_DATA = new byte[] { 0x0B, 0x63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public final static byte[] BRIGHTNES_DATA = new byte[] { 0x05, 0x55, (byte) 0xAA, (byte) 0xD1, 0x01, 0x63, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static byte[] PAGE_1_HEADER = new byte[] { 0x01, 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x42, 0x4D, (byte) 0xF6, 0x3C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00,
			0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00,
			0x00, 0x00, 0x00, 0x00, (byte) 0xC0, 0x3C, 0x00, 0x00, (byte) 0xC4, 0x0E, 0x00, 0x00, (byte) 0xC4, 0x0E,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static byte[] PAGE_2_HEADER = new byte[] { 0x01, 0x02, 0x00, 0x01, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	
	public final static int BUTTON_COUNT = 15;
	
	public final static int ICON_SIZE = 72;

	public final static int PAGE_PACKET_SIZE = 8190;
	
	public final static int NUM_FIRST_PAGE_PIXELS = 2583;
	
	public final static int NUM_SECOND_PAGE_PIXELS = 2601;
	
	public final static BufferedImage BLACK_ICON = createBlackIcon();
	
	
	
	private HidDevice hidDevice = null;
	
	private byte[] brightness = BRIGHTNES_DATA;

	private StreamItem[] keys = new StreamItem[15];
	
	private boolean[] keysPressed = new boolean[15];
	
	
//	private ExecutorService pool = Executors.newSingleThreadExecutor();
	Queue<Runnable> pool = new ConcurrentLinkedQueue<>();

    private byte[] p1 = new byte[PAGE_PACKET_SIZE];

    private byte[] p2 = new byte[PAGE_PACKET_SIZE];

    private Thread thread = null;
    
    private boolean running = true;
    
	
    /**
     * Creates a wrapper for the Stream Deck HID
     * @param streamDeck	Stream Decks HID Devices 
     * @param brightness	Brightness from 0 .. 99
     */
	public StreamDeck(HidDevice streamDeck, int brightness) {
		super();
		this.hidDevice = streamDeck;
		this.hidDevice.setInputReportListener(this);
		this.brightness[5] = (byte)brightness;
		this.thread = new Thread(new DeckWorker());
		this.thread.start();
	}	
	
	private static BufferedImage createBlackIcon() {
		BufferedImage img = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
		g.dispose();
		return img;
	}

	public void drawImage(int keyIndex, BufferedImage img) {
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[ICON_SIZE * ICON_SIZE * 3];
		int imgDataCount=0;
		// remove the alpha channel
		for(int i=0;i<ICON_SIZE*ICON_SIZE; i++) {
			//RGB -> BGR
			imgData[imgDataCount++] = (byte)((pixels[i]>>16) & 0xFF);
			imgData[imgDataCount++] = (byte)(pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte)((pixels[i]>>8) & 0xFF);			
		}
		byte[] page1 = generatePage1(keyIndex, imgData);
		byte[] page2 = generatePage2(keyIndex, imgData);
		this.hidDevice.setOutputReport((byte)0x02, page1, page1.length);
		this.hidDevice.setOutputReport((byte)0x02, page2, page2.length);
	}
	
	public void reset() {
		this.pool.add(new Resetter());
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null)
				this.pool.add(new IconUpdater(i, keys[i].getIcon()));
		}
	}
	
	public void addKey(int keyId, StreamItem item) {
		if (keyId < this.keys.length && keyId >= 0) {
			this.keys[keyId] = item;
			pool.add(new IconUpdater(keyId, item.getIcon()));
		}
	}
	
	public void removeKey(int keyId) {
		if (keyId < this.keys.length && keyId >= 0 && this.keys[keyId] != null) {
			this.keys[keyId] = null;
			pool.add(new IconUpdater(keyId, BLACK_ICON));
		}
	}

	private void _reset() {
		hidDevice.setFeatureReport(RESET_DATA, RESET_DATA.length);
	}
	
	public void setBrightness(int brightness) {
		brightness = brightness > 99 ? 99 : brightness < 0 ? 0 : brightness;
		this.brightness[5] = (byte)brightness;
		this.pool.add(new BrightnessUpdater());
	}

	private void _updateBrightnes() {
		hidDevice.setFeatureReport(this.brightness, this.brightness.length);
	}

	public void waitForCompletion() {
		this.running = false;
		long time = 0;
		while (this.pool.size() != 0) {
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

	private void fireKeyChangedEvent(int i, boolean keyPressed) {
		if (i < this.keys.length && this.keys[i] != null) {
			if (keyPressed)
				this.keys[i].onPress();
			else {
				this.keys[i].onRelease();
				this.keys[i].onClick();
			}
		}
	}
	
	private byte[] generatePage1(int keyId, byte[] imgData)
    {
        for (int i = 0; i < PAGE_1_HEADER.length; i++) {
			p1[i] = PAGE_1_HEADER[i];
		}
        if (imgData != null) {
//            byte[] imgDataPage1 = Arrays.copyOf(imgData, NUM_FIRST_PAGE_PIXELS * 3);
            for (int i = 0; i < imgData.length && i < NUM_FIRST_PAGE_PIXELS * 3; i++) {
				p1[PAGE_1_HEADER.length+i] = imgData[i];
			}
        }
        p1[4] = (byte)(keyId + 1);
        return p1;
    }
	
	private byte[] generatePage2(int keyId, byte[] imgData)
    {
        for (int i = 0; i < PAGE_2_HEADER.length; i++) {
			p2[i] = PAGE_2_HEADER[i];
		}
        if (imgData != null) {
            byte[] imgDataPage2 = Arrays.copyOfRange(imgData, NUM_FIRST_PAGE_PIXELS * 3, (NUM_FIRST_PAGE_PIXELS * 3) + (NUM_SECOND_PAGE_PIXELS * 3));
            for (int i = 0; i < imgDataPage2.length; i++) {
				p2[PAGE_2_HEADER.length+i] = imgDataPage2[i];
			}
        }
        p2[4] = (byte)(keyId + 1);
        return p2;
    }

	private class IconUpdater implements Runnable {
		
		int keyIndex; BufferedImage img;
		
		public IconUpdater(int keyIndex, BufferedImage img) {
			this.keyIndex =keyIndex;
			this.img = img;
		}

		@Override
		public void run() {
			StreamDeck.this.drawImage(keyIndex, img);
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
	
	private class BrightnessUpdater implements Runnable {
		public BrightnessUpdater() {
		}
		@Override
		public void run() {
			StreamDeck.this._updateBrightnes();
		}
	}
	
	private class DeckWorker implements Runnable{

		@Override
		public void run() {
			while(running || !running && !pool.isEmpty()) {
				Runnable task = pool.poll();
				if (task != null) {
					try {
						task.run();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
