package de.rcblum.stream.deck;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

public class HidDevices {
	
	public static final int VENDOR_ID = 4057;
	public static final int PRODUCT_ID = 96;

	private static HidDeviceInfo STREAM_DECK_INFO = null;

	private static HidDevice STREAM_DECK_DEVICE = null;
	
	private static StreamDeck STREAM_DECK = null;
	
	
	public static HidDeviceInfo getStreamDeckInfo() {
		if (STREAM_DECK_INFO == null) {
			System.out.println("scanning");
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
			for (HidDeviceInfo info : devList) {
				System.out.println("Vendor-ID: " + info.getVendorId() + ", Product-ID: " + info.getProductId());
				if (info.getVendorId() == VENDOR_ID && info.getProductId() == PRODUCT_ID) {
					STREAM_DECK_INFO = info;
					break;
				}
			}
		}
		return STREAM_DECK_INFO;
	}
	
	public static HidDevice getStreamDeckDevice() {
		if (STREAM_DECK_DEVICE == null) {
			HidDeviceInfo info = getStreamDeckInfo();
			if (info != null) {
				try {
					STREAM_DECK_DEVICE = PureJavaHidApi.openDevice(STREAM_DECK_INFO);
					STREAM_DECK_DEVICE.setInputReportListener(new InputReportListener() {
						
						@Override
						public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
							System.out.println(reportID + ": " + bytesToHex(reportData));
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return STREAM_DECK_DEVICE;
	}
	
	public static StreamDeck getStreamDeck() {
		if (STREAM_DECK == null) {
			HidDevice dev = getStreamDeckDevice();
			if (dev != null)
				STREAM_DECK = new StreamDeck(STREAM_DECK_DEVICE, 99);
		}
		return STREAM_DECK;
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		HidDeviceInfo streamDeckInfo = getStreamDeckInfo();
		HidDevice streamDeck = getStreamDeckDevice();
		
		System.out.println(streamDeckInfo.getManufacturerString());
		System.out.println(streamDeckInfo.getReleaseNumber());
		System.out.println(streamDeckInfo.getPath());
//		byte[] RESET_DATA = new byte[]{0x0B, 0x63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//		byte[] BRIGHTNES_DATA = new byte[]{0x05, 0x55, (byte)0xAA, (byte)0xD1, 0x01, 0x7F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
//		streamDeck.setFeatureReport(RESET_DATA, RESET_DATA.length);
//		streamDeck.setFeatureReport(BRIGHTNES_DATA, BRIGHTNES_DATA.length);
//		StreamButton sB = new StreamButton(0, Color.BLACK, streamDeck);
		BufferedImage img = ImageIO.read(new File("resources" + File.separator + "overwatch.png"));
		img = IconHelper.createResizedCopy(IconHelper.fillBackground(IconHelper.rotate180(img), Color.BLACK));
		System.out.println(img.getWidth() + ":" + img.getHeight());
//		img = rotate180(img);
//		sB.drawImage(img);
		StreamDeck deck = new StreamDeck(streamDeck, 99);
		ExecutableButton executableButton = new ExecutableButton(4, img,"C:\\elgato\\stream-deck\\shortcuts\\OW.bat");
		deck.reset();
		deck.setBrightness(98);
		deck.addKey(0, executableButton);
		Thread.sleep(500);
		deck.removeKey(0);
		deck.addKey(1, executableButton);
		Thread.sleep(500);
		deck.removeKey(1);
		deck.addKey(2, executableButton);
		Thread.sleep(500);
		deck.removeKey(2);
		deck.addKey(3, executableButton);
		Thread.sleep(500);
		deck.removeKey(3);
		deck.addKey(4, executableButton);
		Thread.sleep(500);
		deck.removeKey(4);
		deck.addKey(5, executableButton);
		Thread.sleep(500);
		deck.removeKey(5);
		deck.addKey(6, executableButton);
		Thread.sleep(500);
		deck.removeKey(6);
		deck.addKey(7, executableButton);
		Thread.sleep(500);
		deck.removeKey(7);
		deck.addKey(8, executableButton);
		Thread.sleep(500);
		deck.removeKey(8);
		deck.addKey(9, executableButton);
		Thread.sleep(500);
		deck.removeKey(9);
		deck.addKey(10, executableButton);
		Thread.sleep(500);
		deck.removeKey(10);
		deck.addKey(11, executableButton);
		Thread.sleep(500);
		deck.removeKey(11);
		deck.addKey(12, executableButton);
		Thread.sleep(500);
		deck.removeKey(12);
		deck.addKey(13, executableButton);
		Thread.sleep(500);
		deck.removeKey(13);
		deck.addKey(14, executableButton);
		Thread.sleep(500);
		deck.removeKey(14);
		deck.reset();
		deck.waitForCompletion();
//		System.exit(0);
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
	
	
}
 