package de.rcblum.stream.deck.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains the image data for the StreamDeck and the Image in a
 * format that can be displayed on pc
 *
 * <br>
 * <br>
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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
public class SDImage {
	
	public final Dimension imageSize;
	
	/**
	 * Image data for the stream deck
	 */
	public final byte[] sdImage;
	
	/**
	 * Image data for the stream deck
	 */
	public final byte[] sdImageJpeg;
	
	/**
	 * Image pre-format transformation
	 */
	public final BufferedImage image;
	
	private Map<Dimension, SDImage> variants = new HashMap<Dimension, SDImage>();

	public SDImage(byte[] sdImage, byte[] sdImageJpeg, BufferedImage image) {
		super();
		imageSize = new Dimension(image.getWidth(), image.getHeight());
		this.sdImage = sdImage;
		this.sdImageJpeg = sdImageJpeg;
		this.image = image;
	}
	
	public void putVariant(Dimension imageSize, SDImage image) {
		this.variants.put(imageSize, image);
	}
	
	public SDImage getVariant(Dimension imageSize) {
		if (this.imageSize.equals(imageSize))
			return this;
		if (!variants.containsKey(imageSize)) {
			SDImage newVariant = IconHelper.convertImage(this.image, imageSize);
			// Backlink to all known variants to prevent unnecessary conversions and memory usage
			this.variants.keySet().stream().forEach(k -> newVariant.putVariant(k, this.variants.get(k)));
			newVariant.putVariant(this.imageSize, this);
			this.putVariant(imageSize, newVariant);
		}
		return variants.get(imageSize);
	}

	public SDImage copy() {
		byte[] sdImage     = new byte[this.sdImage.length];
		byte[] sdImageJpeg = new byte[this.sdImageJpeg.length];
		System.arraycopy(this.sdImage,     0, sdImage,     0, this.sdImage.length);
		System.arraycopy(this.sdImageJpeg, 0, sdImageJpeg, 0, this.sdImageJpeg.length);
		return new SDImage(sdImage, sdImageJpeg, IconHelper.copyBufferedImage(this.image));
	}

}
