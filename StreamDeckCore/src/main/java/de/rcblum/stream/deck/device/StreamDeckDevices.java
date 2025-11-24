package de.rcblum.stream.deck.device;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.device.general.SoftStreamDeck;
import de.rcblum.stream.deck.device.general.StreamDeck;
import de.rcblum.stream.deck.device.general.StreamDeckRev2;
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
	private static boolean enableSoftwareStreamDeck = true; 
	
	private static final Logger LOGGER = LogManager.getLogger(StreamDeckDevices.class);
	
	public static final int VENDOR_ID = 4057;
	
	public static final int PRODUCT_ID = 96;
	
	public static final int[][] DECK_FAMILY = {
			//PRODUCT_ID, KEYS, ROWS, REV
			{         96,   15,    3,   1}, 
			{         99,    6,    2,   1}, 
			{        108,   32,    4,   2}, 
			{        109,   15,    3,   2}
	};
	
	public static final List<Integer> PRODUCT_IDS = Arrays.stream(DECK_FAMILY, 0, DECK_FAMILY.length).map(a -> Integer.valueOf(a[0])).collect(Collectors.toList());

	private static List<HidDeviceInfo> deckInfos = null;

	private static List<HidDevice> deckDevices = null;

	private static List<IStreamDeck> decks = null;

	private static List<IStreamDeck> softDecks = null;
	
	
	public static void enableSoftwareStreamDeck() {
		StreamDeckDevices.enableSoftwareStreamDeck = true;
	}
	
	public static void disableSoftwareStreamDeck() {
		StreamDeckDevices.enableSoftwareStreamDeck = false;
	}
	
	public static boolean isSoftwareStreamDeckEnabled() {
		return StreamDeckDevices.enableSoftwareStreamDeck;
	}
	
	
	public static HidDeviceInfo getStreamDeckInfo() {
		if (deckInfos == null) {
			deckInfos = new ArrayList<>(5);
			LOGGER.info("Scanning for devices");
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
			for (HidDeviceInfo info : devList) {
				LOGGER.debug("Vendor-ID: " + info.getVendorId() + ", Product-ID: " + info.getProductId());
				if (info.getVendorId() == VENDOR_ID && PRODUCT_IDS.contains(Integer.valueOf(info.getProductId()))) {
					LOGGER.info("Found ESD ["+info.getVendorId()+":"+info.getProductId()+"]");
					deckInfos.add(info);
				}
			}
		}
		return !deckInfos.isEmpty() ? deckInfos.get(0) : null;
	}
	
	public static HidDevice getStreamDeckDevice() {
		initStreamDecks();
		return !deckDevices.isEmpty() ? deckDevices.get(0) : null;
	}

	private static void initStreamDecks() {
		if (deckDevices == null || deckDevices.isEmpty()) {
			HidDeviceInfo info = getStreamDeckInfo();
			deckDevices = new ArrayList<>(deckInfos.size());
			if (info != null) {
				try {
					LOGGER.info("Connected Stream Decks:");
					for (HidDeviceInfo hidDeviceinfo : deckInfos) {
						LOGGER.info("  Manufacurer: " + hidDeviceinfo.getManufacturerString());
						LOGGER.info("  Product:     " + hidDeviceinfo.getProductString());
						LOGGER.info("  Device-Id:   " + hidDeviceinfo.getDeviceId());
						LOGGER.info("  Serial-No:   " + hidDeviceinfo.getSerialNumberString());
						LOGGER.info("  Path:        " + hidDeviceinfo.getPath());
						LOGGER.info("");
						deckDevices.add(PureJavaHidApi.openDevice(hidDeviceinfo));
					}
				} catch (IOException e) {
					LOGGER.error("IO Error occured while searching for devices: ", e);
				}
			}
		}
	}
	
	public static IStreamDeck getStreamDeck() {
		if (decks == null || decks.isEmpty()) {
			HidDevice dev = getStreamDeckDevice();
			decks = new ArrayList<>(deckDevices.size());
			if (dev != null) {
				for (HidDevice hidDevice : deckDevices) {
					int[] DECK_DATA = DECK_FAMILY[PRODUCT_IDS.indexOf(Integer.valueOf(hidDevice.getHidDeviceInfo().getProductId()))];
					IStreamDeck streamDeck = null;
					switch (DECK_DATA[3]) {
					case 1:
						streamDeck = new StreamDeck(hidDevice, 99, DECK_DATA[1], DECK_DATA[2]);
						break;
					case 2:
						streamDeck = new StreamDeckRev2(hidDevice, 99, DECK_DATA[1], DECK_DATA[2]);
						break;
					default:
						streamDeck = new StreamDeck(hidDevice, 99, DECK_DATA[1], DECK_DATA[2]);
						break;
					}
					decks.add(streamDeck);
				}
			}
		}
		if(enableSoftwareStreamDeck && !GraphicsEnvironment.isHeadless() && softDecks == null) {
			softDecks = new ArrayList<>(deckDevices.size()); 
			for (int i=0; i<decks.size(); i++) {
				IStreamDeck iStreamDeck = decks.get(i);
				softDecks.add(new SoftStreamDeck("Stream Deck " + i, iStreamDeck));
			}
		}
		return !decks.isEmpty()
				? (enableSoftwareStreamDeck && !GraphicsEnvironment.isHeadless() ? softDecks.get(0) : decks.get(0)) 
				: (enableSoftwareStreamDeck && !GraphicsEnvironment.isHeadless() ? new SoftStreamDeck("Soft Stream Deck", null, 32, 4, true) : null);
	}
	
	public static int getStreamDeckSize() {
		return decks != null ? decks.size() : 0;
	}
	
	public static IStreamDeck getStreamDeck(int id) {
		if (decks == null || id < 0 || id >= getStreamDeckSize())
			return null;
		return decks.get(id);
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
 