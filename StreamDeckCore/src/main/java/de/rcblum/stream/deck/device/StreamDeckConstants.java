package de.rcblum.stream.deck.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

public class StreamDeckConstants {
	

	private static final Logger LOGGER = LogManager.getLogger(StreamDeckConstants.class);
	
    // REV2 Data

	/**
     * Reset command
     */
	public static final byte[] RESET_DATA_REV2 = new byte[]{
    		0x03, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /**
     * Brightness command
     */
    public static final byte[] BRIGHTNES_DATA_REV2 = new byte[]{
            0x03, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    };

    /**
     * Header for all pages of the image command to update a single key
     */
    private static final byte[] IMAGE_PAGE_HEADER_REV2 = new byte[]{
    		0x02, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /**
     * Header for all pages of the image command to update all keys with one image
     */
    private static final byte[] IMAGE_PAGE_HEADER_ALL_REV2 = new byte[]{
    		0x02, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /**
     * Page size that can be sent to the ESD at once
     */
    public static final int PAGE_PACKET_SIZE_REV2 = 1024;

    /**
     * REV2 Input Report for normal Keys
     */
    public static final byte[] INPUT_REPORT_IMAGE_KEY_REV2 = new byte[] {
    		0x00, 0x08, 0x00
    };
    
    /**
     * REV2 Input Report for dials Keys
     */
    public static final byte[] INPUT_REPORT_DIAL_REV2 = new byte[] {
    		0x03, 0x05, 0x00
    };
    
    /**
     * REV2 Input Report for dials Keys
     */
    public static final byte[] INPUT_REPORT_TOUCH_SCREEN_REV2 = new byte[] {
    		0x02, 0x0E, 0x00
    };
    
    // REV1 Data
    
	/**
	 * Header for Page 1 of the image command
	 */
	private static final byte[] PAGE_1_HEADER_REV1 = new byte[] { 0x01, 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x42, 0x4D, (byte) 0xF6, 0x3C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00,
			0x00, 0x28, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00,
			0x00, 0x00, 0x00, (byte) 0xC0, 0x3C, 0x00, 0x00, (byte) 0xC4, 0x0E, 0x00, 0x00, (byte) 0xC4, 0x0E, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	/**
	 * Header for Page 2 of the image command
	 */
	private static final byte[] PAGE_2_HEADER_REV1 = new byte[] { 
			0x01, 0x02, 0x00, 0x01, 0x06, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 
	};

	/**
	 * Brightness command
	 */
	public static final byte[] BRIGHTNES_DATA_REV1 = new byte[] { 
			0x05, 0x55, (byte) 0xAA, (byte) 0xD1, 0x01, 0x63, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 
	};

	/**
	 * Reset command
	 */
	public static final byte[] RESET_DATA_REV1 = new byte[] { 
			0x0B, 0x63, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 
	};

    /**
     * REV1 Input Report for normal Keys
     */
    public static final byte[] INPUT_REPORT_IMAGE_KEY_REV1 = new byte[] {
    };
    
    /**
     * REV1 Input Report for dials Keys
     */
    public static final byte[] INPUT_REPORT_DIAL_REV1 = null;
    
    /**
     * REV1 Input Report for dials Keys
     */
    public static final byte[] INPUT_REPORT_TOUCH_SCREEN_REV1 = null;
	
	/**
	 * Page size that can be sent to the ESD at once
	 */
	public static final int PAGE_PACKET_SIZE_REV1 = 8190;

	/**
	 * Pixels(times 3 to get the amount of bytes) of an icon that can be sent with page 1 of the image command
	 */
	public static final int NUM_FIRST_PAGE_PIXELS_REV1 = 2583;

	/**
	 * Pixels(times 3 to get the amount of bytes) of an icon that can be sent with page 2 of the image command
	 */
	public static final int NUM_SECOND_PAGE_PIXELS_REV1 = 2601;

	/**
	 * Number of buttons on the ESD, assuming the standard ESD
	 */
	public static final int BUTTON_COUNT = 15;
	
	/**
	 * Number of rows on the ESD, assuming the standard ESD
	 */
	public static final int ROW_COUNT = 3;

	/**
	 * Icon size of one key (Use the greatest size)
	 */
	public static final Dimension ICON_SIZE = new Dimension(120, 120);

	/**
	 * Back image for not used keys
	 */
	public static final SDImage BLACK_ICON = createBlackIcon("temp://BLACK_ICON");
	
	/**
	 * Cache for image data output reports to prevent unnecessary object creation
	 */
	private static Map<String, byte[][]> PAGE_CACHE = new HashMap<String, byte[][]>(); 

	/**
	 * Creates a black 72x72 image and caches it for the given path
	 * @param path path under which the image is cached
	 * @return
	 */
	private static SDImage createBlackIcon(String path) {
		BufferedImage img = new BufferedImage((int)ICON_SIZE.getWidth(), (int)ICON_SIZE.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, (int)ICON_SIZE.getWidth(), (int)ICON_SIZE.getHeight());
		g.dispose();
		return IconHelper.cacheImage(path, img);
	}

	/**
	 * Sends reset-command to ESD REV2
	 */
	public static void internalResetRev2(HidDevice hidDevice) {
		hidDevice.setFeatureReport(RESET_DATA_REV2[0], Arrays.copyOfRange(RESET_DATA_REV2, 1, RESET_DATA_REV2.length), RESET_DATA_REV2.length-1);
	}

	/**
	 * Sends brightness-command to ESD REV2
	 */
	public static void internalUpdateBrightnessRev2(HidDevice hidDevice, int brightnessValue) {
		byte[] brightness = BRIGHTNES_DATA_REV2;
		brightnessValue = brightnessValue > 99 ? 99 : brightnessValue < 0 ? 0 : brightnessValue;
		brightness[2] = (byte) brightnessValue;
		hidDevice.setFeatureReport(brightness[0], Arrays.copyOfRange(brightness, 1, brightness.length), brightness.length-1);
	}

	/**
	 * Sends reset-command to ESD REV1
	 */
	public static void internalResetRev1(HidDevice hidDevice) {
		hidDevice.setFeatureReport(RESET_DATA_REV1[0], Arrays.copyOfRange(RESET_DATA_REV1, 1, RESET_DATA_REV1.length), RESET_DATA_REV1.length-1);
	}

	/**
	 * Sends brightness-command to ESD REV1
	 */
	public static void internalUpdateBrightnessRev1(HidDevice hidDevice, int brightnessValue) {
		byte[] brightness = BRIGHTNES_DATA_REV1;
		brightnessValue = brightnessValue > 99 ? 99 : brightnessValue < 0 ? 0 : brightnessValue;
		brightness[5] = (byte) brightnessValue;
		hidDevice.setFeatureReport(brightness[0], Arrays.copyOfRange(brightness, 1, brightness.length), brightness.length-1);
	}

    public static synchronized void internalDrawImageRev2(HidDevice hidDevice, int keyIndex, Dimension iconSize, SDImage imgData) {
    	internalDrawImageRev2(hidDevice, keyIndex, iconSize, imgData, IMAGE_PAGE_HEADER_REV2);
    }

    public static synchronized void internalDrawFullImageRev2(HidDevice hidDevice, int keyIndex, Dimension iconSize, SDImage imgData) {
    	internalDrawImageRev2(hidDevice, keyIndex, iconSize, imgData, IMAGE_PAGE_HEADER_ALL_REV2);
    }

    public static synchronized void internalDrawImageRev2(HidDevice hidDevice, int keyIndex, Dimension iconSize, SDImage imgData, byte[] pageHeader) {
		imgData = imgData.getVariant(iconSize);
		if (PAGE_CACHE.get(hidDevice.getHidDeviceInfo().getPath()) == null) {
			PAGE_CACHE.put(hidDevice.getHidDeviceInfo().getPath(), new byte[][]{
					new byte[PAGE_PACKET_SIZE_REV2]
			});
		}
        int pageLength = PAGE_PACKET_SIZE_REV2 - pageHeader.length;
        int pages = (int) Math.ceil(((float) imgData.sdImageJpeg.length) / pageLength);
        byte[] report = PAGE_CACHE.get(hidDevice.getHidDeviceInfo().getPath())[0];
        for (int i = 0; i < pageHeader.length; i++) {
            report[i] = pageHeader[i];
        }
        // Send Image in split reports
        for (int pageNo = 0; pageNo < pages; pageNo++) {
        	int byteFrom = pageNo * pageLength;
        	int byteTo = Math.min(((pageNo + 1) * pageLength), imgData.sdImageJpeg.length);
        	int payloadLength = byteTo - byteFrom;
            //byte[] page = Arrays.copyOfRange(imgData.sdImageJpeg, pageNo * pageLength, Math.min(((pageNo + 1) * pageLength), imgData.sdImageJpeg.length));
            for (int j = 0; j < byteTo - byteFrom; j++) {
                report[pageHeader.length + j] = imgData.sdImageJpeg[j + byteFrom];
            }
            // Key to be updated

            report[2] = (byte) keyIndex;
            // 0 = More pages are beeing sent, 1 = this is the last page of the image
            report[3] = pageNo < pages - 1 ? (byte) 0x00 : (byte) 0x01;
            // Length of the payload sent
            report[4] = (byte) (payloadLength & 0xff);
            report[5] = (byte) ((payloadLength >> 8) & 0xff);
            // Number of the page sent
            report[6] = (byte) (pageNo & 0xff);
            report[7] = (byte) ((pageNo >> 8) & 0xff);

            int result = hidDevice.setOutputReport((byte) report[0], Arrays.copyOfRange(report, 1, report.length), report.length - 1);
            //LOGGER.debug("key " + keyIndex + ", frame " + pageNo + " => " + result);
            if (result < 0) {
                break;
            }
        }
        //LOGGER.debug("Image Length: " + imgData.sdImageJpeg.length);
        //System.out.println("total bytes: " + totalBytes);
    }

	public static synchronized void internalDrawImageRev1(HidDevice hidDevice, int keyIndex, Dimension iconSize, SDImage imgData) {
		imgData = imgData.getVariant(iconSize);
		if (PAGE_CACHE.get(hidDevice.getHidDeviceInfo().getPath()) == null) {
			PAGE_CACHE.put(hidDevice.getHidDeviceInfo().getPath(), new byte[][]{
					new byte[PAGE_PACKET_SIZE_REV1],
					new byte[PAGE_PACKET_SIZE_REV1]
			});
		}
		byte[] page1 = generatePage1(keyIndex, PAGE_CACHE.get(hidDevice.getHidDeviceInfo().getPath())[0], imgData.sdImage);
		byte[] page2 = generatePage2(keyIndex, PAGE_CACHE.get(hidDevice.getHidDeviceInfo().getPath())[1], imgData.sdImage);
		hidDevice.setOutputReport((byte) 0x02, page1, page1.length);
		hidDevice.setOutputReport((byte) 0x02, page2, page2.length);
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
	private static byte[] generatePage1(int keyId, byte[] page1, byte[] imgData) {
		for (int i = 0; i < PAGE_1_HEADER_REV1.length; i++) {
			page1[i] = PAGE_1_HEADER_REV1[i];
		}
		if (imgData != null) {
			for (int i = 0; i < imgData.length && i < NUM_FIRST_PAGE_PIXELS_REV1 * 3; i++) {
				page1[PAGE_1_HEADER_REV1.length + i] = imgData[i];
			}
		}
		page1[4] = (byte) (keyId + 1);
		return page1;
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
	private static byte[] generatePage2(int keyId, byte[] page2, byte[] imgData) {
		for (int i = 0; i < PAGE_2_HEADER_REV1.length; i++) {
			page2[i] = PAGE_2_HEADER_REV1[i];
		}
		if (imgData != null) {
			for (int i = 0; i < NUM_SECOND_PAGE_PIXELS_REV1 * 3 && i < imgData.length; i++) {
				page2[PAGE_2_HEADER_REV1.length + i] = imgData[(NUM_FIRST_PAGE_PIXELS_REV1 * 3) + i];
			}
		}
		page2[4] = (byte) (keyId + 1);
		return page2;
	}
}
