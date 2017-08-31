package de.rcblum.stream.deck;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class IconHelper {
	
	public static BufferedImage fillBackground(BufferedImage img, Color color) {
		BufferedImage nImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = nImg.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, nImg.getWidth(), nImg.getHeight());
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return nImg;
	}
	
	public static BufferedImage rotate180(BufferedImage inputImage) {
		// The most of code is same as before
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage(height, width, inputImage.getType());
		// We have to change the width and height because when you rotate the
		// image by 90 degree, the
		// width is height and height is width <img
		// src='http://forum.codecall.net/public/style_emoticons/<#EMO_DIR#>/smile.png'
		// class='bbc_emoticon' alt=':)' />

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnImage.setRGB(height - x - 1, width - y - 1, inputImage.getRGB(x, y));
				// Again check the Picture for better understanding
			}
		}
		BufferedImage returnImage2 = new BufferedImage(height, width, inputImage.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnImage2.setRGB(x, height - y - 1, returnImage.getRGB(x, y));
				// Again check the Picture for better understanding
			}
		}
		return returnImage2;
	}
	
	public static BufferedImage createResizedCopy(BufferedImage originalImage)
    {
		int scaledWidth = 72;
		int scaledHeight = 72;
		if (originalImage.getWidth() != originalImage.getHeight()) {
			float scalerWidth = 72f/originalImage.getWidth();
			float scalerHeight = 72f/originalImage.getWidth();
			if (scalerWidth < scaledHeight)
				scaledHeight = Math.round(scalerWidth * originalImage.getHeight());
			else
				scaledWidth = Math.round(scalerHeight * originalImage.getWidth());
			
		}
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (true) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
        g.dispose();
        return scaledBI;
    }
}
