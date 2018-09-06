package de.rcblum.stream.deck.util;

import java.awt.image.BufferedImage;

/**
 * Class that contains the image data for the StreamDeck and the Image in a
 * format that can be displayed on pc
 * 
 * @author VV3IRD
 *
 */
public class SDImage {
	
	/**
	 * Image data for the stream deck
	 */
	public final byte[] sdImage;
	
	/**
	 * Image pre format transformation
	 */
	public final BufferedImage image;

	public SDImage(byte[] sdImage, BufferedImage image) {
		super();
		this.sdImage = sdImage;
		this.image = image;
	}

}
