package de.rcblum.stream.deck.device;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.PureJavaHidApi;

/**
 * 
 * <br><br>
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
 * @version 1.0.0
 *
 */
public class StreamDeckDevices {
	
	/**
	 * Flag for enabling the software stream deck GUI. <code>true</code> Stream Deck
	 * devices will be wrapped in a software SD, <code>false</code> the StreamDeck
	 * will be returned directly.
	 */
	public static boolean ENABLE_SOFTWARE_STREAM_DECK = true; 
	
	private static final Logger LOGGER = LogManager.getLogger(StreamDeckDevices.class);
	
	public static final int VENDOR_ID = 4057;
	
	public static final int PRODUCT_ID = 96;

	private static List<HidDeviceInfo> streamDeckInfos = null;

	private static List<HidDevice> streamDeckDevices = null;

	private static List<IStreamDeck> streamDecks = null;

	private static List<IStreamDeck> softStreamDecks = null;
	
	
	public static HidDeviceInfo getStreamDeckInfo() {
		if (streamDeckInfos == null) {
			streamDeckInfos = new ArrayList<>(5);
			LOGGER.info("Scanning for devices");
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
			for (HidDeviceInfo info : devList) {
				LOGGER.debug("Vendor-ID: " + info.getVendorId() + ", Product-ID: " + info.getProductId());
				if (info.getVendorId() == VENDOR_ID && info.getProductId() == PRODUCT_ID) {
					LOGGER.info("Found ESD ["+info.getVendorId()+":"+info.getProductId()+"]");
					streamDeckInfos.add(info);
				}
			}
		}
		return !streamDeckInfos.isEmpty() ? streamDeckInfos.get(0) : null;
	}
	
	public static HidDevice getStreamDeckDevice() {
		if (streamDeckDevices == null || streamDeckDevices.isEmpty()) {
			HidDeviceInfo info = getStreamDeckInfo();
			streamDeckDevices = new ArrayList<>(streamDeckInfos.size());
			if (info != null) {
				try {
					LOGGER.info("Connected Stream Decks:");
					for (HidDeviceInfo hidDeviceinfo : streamDeckInfos) {
						LOGGER.info("  Manufacurer: " + hidDeviceinfo.getManufacturerString());
						LOGGER.info("  Product:     " + hidDeviceinfo.getProductString());
						LOGGER.info("  Device-Id:   " + hidDeviceinfo.getDeviceId());
						LOGGER.info("  Serial-No:   " + hidDeviceinfo.getSerialNumberString());
						LOGGER.info("  Path:        " + hidDeviceinfo.getPath());
						LOGGER.info("");
						streamDeckDevices.add(PureJavaHidApi.openDevice(hidDeviceinfo));
					}
				} catch (IOException e) {
					LOGGER.error("IO Error occured while searching for devices: ", e);
				}
			}
		}
		return !streamDeckDevices.isEmpty() ? streamDeckDevices.get(0) : null;
	}
	
	public static IStreamDeck getStreamDeck() {
		if (streamDecks == null || streamDecks.isEmpty()) {
			HidDevice dev = getStreamDeckDevice();
			streamDecks = new ArrayList<>(streamDeckDevices.size());
			if (dev != null) {
				for (HidDevice hidDevice : streamDeckDevices) {
					streamDecks.add(new StreamDeck(hidDevice, 99, StreamDeck.BUTTON_COUNT));
				}
			}
		}
		if(ENABLE_SOFTWARE_STREAM_DECK && !GraphicsEnvironment.isHeadless() && softStreamDecks == null) {
			softStreamDecks = new ArrayList<>(streamDeckDevices.size()); 
			for (int i=0; i<streamDecks.size(); i++) {
				IStreamDeck iStreamDeck = streamDecks.get(i);
				softStreamDecks.add(new SoftStreamDeck("Stream Deck " + i, iStreamDeck));
			}
		}
		return !streamDecks.isEmpty()
				? (ENABLE_SOFTWARE_STREAM_DECK && !GraphicsEnvironment.isHeadless() ? softStreamDecks.get(0) : streamDecks.get(0)) 
				: (ENABLE_SOFTWARE_STREAM_DECK && !GraphicsEnvironment.isHeadless() ? new SoftStreamDeck("Soft Stream Deck", null) : null);
	}
	
	public static int getStreamDeckSize() {
		return streamDecks != null ? streamDecks.size() : 0;
	}
	
	public static IStreamDeck getStreamDeck(int id) {
		if (streamDecks == null || id < 0 || id >= getStreamDeckSize())
			return null;
		return streamDecks.get(id);
	}
	
	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    builder.append("{");
	    for(byte b : in) {
	        builder.append(String.format(" 0x%02x,", b));
	    }
	    builder.append("}");
	    return builder.toString();
	}
	
	private StreamDeckDevices() {
		// Nothing here stanger
	}
}
 