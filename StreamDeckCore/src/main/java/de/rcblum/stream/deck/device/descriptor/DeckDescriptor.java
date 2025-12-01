package de.rcblum.stream.deck.device.descriptor;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rcblum.stream.deck.device.StreamDeckConstants;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawImageInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawTouchscreenInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportIntegerInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportInterface;

/**
 * Descriptor for the different stream decks.
 *   
 * <br><br> 
 * 
 * MIT License<br>
 * <br>
 * 2025 Roland von Werden<br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy<br>
 * of this software and associated documentation files (the "Software"), to deal<br>
 * in the Software without restriction, including without limitation the rights<br>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>
 * copies of the Software, and to permit persons to whom the Software is<br>
 * furnished to do so, subject to the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be included in all<br>
 * copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE<br>
 * SOFTWARE.<br>
 * 
 * @author Roland von Werden
 * @version 1.0.1
 *
 */
public class DeckDescriptor {

	public static DeckDescriptor getDescriptor(short vendorId, short productid) {
		return DESCRIPTORS.stream().filter(d -> d.deviceVendor == vendorId && d.productId == productid).findFirst().orElse(null);
	}
	
	public static DeckDescriptor getDescriptorByKeySize(int keys) {
		return DESCRIPTORS.stream().filter(d -> d.getKeySize() == keys).findFirst().orElse(null);
	}
	
	public static final DeckDescriptor SOFT_STREAM_DECK;
	
	private static final List<DeckDescriptor> DESCRIPTORS;
	
	static {
		SOFT_STREAM_DECK = new DeckDescriptor(
				4057,                                                 // Device Vendor Id
				00,                                                   // Product ID
				"Stream Deck Software",                               // Description
				new Dimension(96, 96),                                // Image size
				18,                                                   // Default font size
				null,                                                 // Image size of the full display
				new KeyType[] {
						KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
						KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
						KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON
				},
				1,                                                    // Offset in index of key when sending images to stream deck
				3,                                                    // Rows
				5,                                                    // Columns
				0,                                                    // Offset for reading key ids in the input report
				null,                                                 // Function to send images to the stream deck
				null,                                                 // Function to send a  image to the stream deck for the touch screen
				null,                                                 // Function to send a full image to the stream deck for the complete streamdeck
				null,                                                 // Function to reset the stream deck
				null,                                                 // Function to update the brightness of the stream deck
				null,                                                 // Input Report for normal keys
				null,                                                 // Input Report for touch screens
				null                                                  // Input Report for dials			
		);
		
		DESCRIPTORS = new ArrayList<DeckDescriptor>(5);
		DESCRIPTORS.add(
			new DeckDescriptor(
					4057,                                                 // Device Vendor Id
					96,                                                   // Product ID
					"Stream Deck Classic Rev1",                           // Description
					new Dimension(72, 72),                                // Image size
					18,                                                   // Default font size
					null,                                                 // Image size of the full display
					new KeyType[] {                                       // Keys
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON
					}, 
					0,                                                    // Offset in index of key when sending images to stream deck
					3,                                                    // Rows
					5,                                                    // Columns
					0,                                                    // Offset for reading key ids in the input report
					StreamDeckConstants::internalDrawImageRev1,           // Function to send images to the stream deck
					null,                                                 // Function to send a  image to the stream deck for the touch screen
					null,                                                 // Function to send a full image to the stream deck for the complete streamdeck
					StreamDeckConstants::internalResetRev1,               // Function to reset the stream deck
					StreamDeckConstants::internalUpdateBrightnessRev1,    // Function to update the brightness of the stream deck
					StreamDeckConstants.INPUT_REPORT_IMAGE_KEY_REV1,      // Input Report for normal keys
					StreamDeckConstants.INPUT_REPORT_TOUCH_SCREEN_REV1,   // Input Report for touch screens
					StreamDeckConstants.INPUT_REPORT_DIAL_REV1            // Input Report for dials			
			)
		);
		DESCRIPTORS.add(
			new DeckDescriptor(
					4057, 
					99, 
					"Stream Deck Mini", 
					new Dimension(80, 80),                                // Image size
					20,                                                   // Default font size
					null,                                                 // Image size of the full display
					new KeyType[] {
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON
					}, 
					0,                                                    // Offset in index of key when sending images to stream deck
					2, 
					3, 
					0,
					StreamDeckConstants::internalDrawImageRev1, 
					null,                                                 // Function to send a  image to the stream deck for the touch screen
					null,                                                 // Function to send a full image to the stream deck for the complete streamdeck
					StreamDeckConstants::internalResetRev1,               // Function to reset the stream deck
					StreamDeckConstants::internalUpdateBrightnessRev1,    // Function to update the brightness of the stream deck
					StreamDeckConstants.INPUT_REPORT_IMAGE_KEY_REV1,      // Input Report for normal keys
					StreamDeckConstants.INPUT_REPORT_TOUCH_SCREEN_REV1,   // Input Report for touch screens
					StreamDeckConstants.INPUT_REPORT_DIAL_REV1            // Input Report for dials		
			)
		);
		DESCRIPTORS.add(
			new DeckDescriptor(
					4057, 
					108, 
					"Stream Deck XL", 
					new Dimension(96, 96),                                // Image size
					22,                                                   // Default font size
					null,                                                 // Image size of the full display
					new KeyType[] {
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON
					}, 
					0,                                                    // Offset in index of key when sending images to stream deck
					4, 
					8, 
					3,
					StreamDeckConstants::internalDrawImageRev2,           // Function to send images to the stream deck
					null,                                                 // Function to send a  image to the stream deck for the touch screen
					null,                                                 // Function to send a full image to the stream deck for the complete stream deck
					StreamDeckConstants::internalResetRev2,               // Function to reset the stream deck
					StreamDeckConstants::internalUpdateBrightnessRev2,    // Function to update the brightness of the stream deck
					StreamDeckConstants.INPUT_REPORT_IMAGE_KEY_REV2,      // Input Report for normal keys
					StreamDeckConstants.INPUT_REPORT_TOUCH_SCREEN_REV2,   // Input Report for touch screens
					StreamDeckConstants.INPUT_REPORT_DIAL_REV2            // Input Report for dials		
			)
		);
		DESCRIPTORS.add(
			new DeckDescriptor(
					4057, 
					109, 
					"Stream Deck Classic Rev2", 
					new Dimension(72, 72),                                // Image size
					18,                                                   // Default font size
					null,                                                 // Image size of the full display
					new KeyType[] {
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON,
							KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON
					}, 
					0,                                                    // Offset in index of key when sending images to stream deck
					3, 
					5, 
					3,
					StreamDeckConstants::internalDrawImageRev2,           // Function to send images to the stream deck
					null,                                                 // Function to send a  image to the stream deck for the touch screen
					null,                                                 // Function to send a full image to the stream deck for the complete stream deck
					StreamDeckConstants::internalResetRev2, 
					StreamDeckConstants::internalUpdateBrightnessRev2,
					StreamDeckConstants.INPUT_REPORT_IMAGE_KEY_REV2,      // Input Report for normal keys
					StreamDeckConstants.INPUT_REPORT_TOUCH_SCREEN_REV2,   // Input Report for touch screens
					StreamDeckConstants.INPUT_REPORT_DIAL_REV2            // Input Report for dials		
			)
		);
		DESCRIPTORS.add(
				new DeckDescriptor(
						4057,                                                 // Device Vendor Id
						132,                                                  // Product ID
						"Stream Deck Plus",                                   // Description
						new Dimension(120, 120),                              // Image size
						26,                                                   // Default font size
						new Dimension(800, 480),                              // Image size of the full display
						new KeyType[] {                                       // Keys
								KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, 
								KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, KeyType.IMAGE_BUTTON, 
								KeyType.TOUCH_SCREEN,
								KeyType.ROTARY_ENCODER, KeyType.ROTARY_ENCODER, KeyType.ROTARY_ENCODER, KeyType.ROTARY_ENCODER
						}, 
						0,                                                    // Offset in index of key when sending images to stream deck
						2,                                                    // Rows
						8,                                                    // Columns
						3,                                                    // Offset for reading key ids in the input report
						StreamDeckConstants::internalDrawImageRev2,           // Function to send images to the stream deck
						StreamDeckConstants::internalDrawTouchScreenRev2,     // Function to send images for the touch screen to the stream deck
						StreamDeckConstants::internalDrawFullImageRev2,       // Function to send a full image to the stream deck for the complete stream deck
						StreamDeckConstants::internalResetRev2,               // Function to reset the stream deck
						StreamDeckConstants::internalUpdateBrightnessRev2,    // Function to update the brightness of the stream deck
						StreamDeckConstants.INPUT_REPORT_IMAGE_KEY_REV2,      // Input Report for normal keys
						StreamDeckConstants.INPUT_REPORT_TOUCH_SCREEN_REV2,   // Input Report for touch screens
						StreamDeckConstants.INPUT_REPORT_DIAL_REV2            // Input Report for dials	
				)
			);
		
	}

	public final short deviceVendor;
	
	public final short productId;
	
	public final String deviceName;
	
	public final Dimension iconSize;
	
	public final Dimension fullDisplaySize;
	
	private final KeyType [] keys;

	public final int rows;

	public final int columns;
	
	public final int defaultFontSize;
	
	public final int touchScreenIndex;
	
	private final KeyType [] specialKeys;
	
	public final byte [] inputReportKeys;
	
	public final byte [] inputReportTouchScreen;
	
	public final byte [] inputReportDials;
	
	public final int keyEventInputReportOffset;

	public final DrawImageInterface drawImageInterface;

	public final int drawImageKeyOffset;
	
	public final DrawTouchscreenInterface drawTouchScreenInterface;
	
	public final DrawImageInterface drawFullImageInterface;
	
	public final FeatureReportInterface resetInterface;
	
	public final FeatureReportIntegerInterface brightnessInterface;
	
	private DeckDescriptor(
			int deviceVendor, int productId, String deviceName, Dimension iconSize, int defaultFontSize, Dimension fullDisplaySize, KeyType [] keys, int drawImageKeyOffset, int rows, int columns, int keyEventInputReportOffset, 
			DrawImageInterface drawIface, DrawTouchscreenInterface drawTouchScreenIface, DrawImageInterface drawFullImageIface, FeatureReportInterface resetIface, FeatureReportIntegerInterface brightnessIface,
			byte [] inputReportKeys, byte [] inputReportTouchScreen, byte [] inputReportDials) {
		this.deviceVendor = (short)deviceVendor;
		this.productId = (short)productId;
		this.deviceName = deviceName;
		this.iconSize = iconSize;
		this.defaultFontSize = defaultFontSize;
		this.fullDisplaySize = fullDisplaySize;
		this.keys = Arrays.stream(keys).filter(k -> !k.isRowless()).map(k -> k.variant(iconSize)).toArray(KeyType[]::new);
		this.specialKeys = Arrays.stream(keys).filter(k -> k.isRowless()).toArray(KeyType[]::new);
		int touchScreenIndex = -1;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(KeyType.TOUCH_SCREEN)) {
				touchScreenIndex = i;
				break;
			}
		}
		this.touchScreenIndex = touchScreenIndex;
		this.drawImageKeyOffset = drawImageKeyOffset;
		if (productId == 132) {
			for (int i = 0; i < this.specialKeys.length; i++) {
				if (this.specialKeys[i].equals(KeyType.TOUCH_SCREEN))
					this.specialKeys[i] = this.specialKeys[i].variant(new Dimension(800, 100));
			}
		}
		this.drawImageInterface = drawIface;
		this.drawTouchScreenInterface = drawTouchScreenIface;
		this.drawFullImageInterface = drawFullImageIface;
		this.resetInterface = resetIface;
		this.brightnessInterface = brightnessIface;
		this.rows = rows;
		this.columns = columns;
		this.keyEventInputReportOffset = keyEventInputReportOffset;
		this.inputReportKeys = inputReportKeys;
		this.inputReportTouchScreen = inputReportTouchScreen;
		this.inputReportDials = inputReportDials;
	}
	
	public int getKeySize() {
		return this.keys.length;
	}
	
	public int getTotalKeySize() {
		return this.keys.length + this.specialKeys.length;
	}
	
	public int getSpecialKeySize() {
		return this.specialKeys.length;
	}
	
	public int getSpecialKeyOffset() {
		return this.keys.length;
	}

	public KeyType getKey(int keyId) {
		if (keyId < this.keys.length)
			return this.keys[keyId];
		else if (keyId < this.getSpecialKeySize() + this.getSpecialKeyOffset())
			return this.specialKeys[keyId - this.getSpecialKeyOffset()];
		else
			return null;
		
	}

	public KeyType getSpecialKey(int keyId) {
		return this.keys[keyId-getSpecialKeySize()];
	}

	public KeyType[] getSpecialKeys() {
		return this.specialKeys;
	}

	public int getTouchScreenIndex() {
		// TODO Auto-generated method stub
		return this.touchScreenIndex;
	}
}
