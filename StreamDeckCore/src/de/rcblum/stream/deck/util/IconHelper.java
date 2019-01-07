package de.rcblum.stream.deck.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.stream.deck.device.StreamDeck;
import de.rcblum.stream.deck.items.animation.AnimationStack;

/**
 * 
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
public class IconHelper {

	private static final String FOLDER_IMAGE_PREFIX = "temp://FOLDER_";

	private static final String TEMP_BLACK_ICON = "temp://BLACK_ICON";

	private static Logger logger = LogManager.getLogger(IconHelper.class);

	/**
	 * Switch for drawing a black rounded frame around all images
	 */
	public static boolean applyFrame = true;
	
	public static Color FRAME_COLOR = Color.BLACK;
	
	public static BufferedImage FRAME = getImageFromResource("/resources/icons/frame.png");

	/**
	 * Default font for the text on the ESD FantasqueSansMono-Bold.ttf
	 * /resources/Blogger-Sans-Medium.ttf /resources/FantasqueSansMono-Bold.ttf
	 */
	public static final Font DEFAULT_FONT = loadFont("/resources/FantasqueSansMono-Regular.ttf", 16);

	/**
	 * Position to place the text at the top of the icon
	 */
	public static final int TEXT_TOP = 0;

	/**
	 * Position to place the text at the center of the icon
	 */
	public static final int TEXT_CENTER = 1;

	/**
	 * Position to place the text bottom the top of the icon
	 */
	public static final int TEXT_BOTTOM = 2;
	
	/**
	 * Sets the padding for the rolling text.
	 */
	private static int rollingTextPadding = 10;
	
	/**
	 * Alpha value for the textbox background
	 */	
	private static int textBoxAlphaValue = 200;

	/**
	 * Cache for loaded images
	 */
	private static Map<String, SDImage> imageCache = new HashMap<>();

	/**
	 * cache for loaded IconPackages
	 */
	private static Map<String, IconPackage> packageCache = new HashMap<>();

	static {
		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.dispose();
		BLACK_ICON = cacheImage(TEMP_BLACK_ICON, img);
		img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.setColor(new Color(132, 132, 132));
		g.drawRect(15, 23, 42, 29);
		g.drawRect(16, 24, 40, 27);
		g.drawLine(53, 22, 40, 22);
		g.drawLine(52, 21, 41, 21);
		g.drawLine(51, 20, 42, 20);
		g.drawLine(50, 19, 43, 19);
		g.dispose();
		FOLDER_ICON = cacheImage("temp://FOLDER", img);
		SDImage back = loadImageFromResource("/resources/icons/back.png");
		cache("temp://BACK", back);
		cache("FRAME_" + FRAME_COLOR.getRGB(), new SDImage(null, FRAME));
	}
	
	public static final SDImage BLACK_ICON;

	public static final SDImage FOLDER_ICON;
	
	
	public static int getTextBoxAlphaValue() {
		return textBoxAlphaValue;
	}
	
	public static void setTextBoxAlphaValue(int textBoxAlphaValue) {
		IconHelper.textBoxAlphaValue = textBoxAlphaValue;
	}
	
	public static int getRollingTextPadding() {
		return rollingTextPadding;
	}
	
	public static void setRollingTextPadding(int rollingTextPadding) {
		IconHelper.rollingTextPadding = rollingTextPadding;
	}
	
	public static SDImage createFolderImage(Color background, boolean applyFrame) {
		if(imageCache.containsKey(FOLDER_IMAGE_PREFIX + background.getRGB()))
			return imageCache.get(FOLDER_IMAGE_PREFIX + background.getRGB());
		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.setColor(new Color(132, 132, 132));
		g.drawRect(15, 23, 42, 29);
		g.drawRect(16, 24, 40, 27); 
		g.drawLine(53, 22, 40, 22);
		g.drawLine(52, 21, 41, 21);
		g.drawLine(51, 20, 42, 20);
		g.drawLine(50, 19, 43, 19);
		g.dispose();
		boolean oldVal = IconHelper.applyFrame;
		IconHelper.applyFrame = applyFrame;
		SDImage sdImage = cacheImage(FOLDER_IMAGE_PREFIX + background.getRGB(), img);
		IconHelper.applyFrame = oldVal;
		return sdImage;
		
	}
	
	public static SDImage createColoredFrame(Color borderColor) {
		if(imageCache.containsKey(FOLDER_IMAGE_PREFIX + borderColor.getRGB()))
			return imageCache.get(FOLDER_IMAGE_PREFIX + borderColor.getRGB());
		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(borderColor);;
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.dispose();
		applyAlpha(img, FRAME);
		return cacheImage(FOLDER_IMAGE_PREFIX + borderColor.getRGB(), img);
	}
	
	public static void applyAlpha(BufferedImage image, BufferedImage mask)
	{
	    int width = image.getWidth();
	    int height = image.getHeight();

	    int[] imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
	    int[] maskPixels = mask.getRGB(0, 0, width, height, null, 0, width);

	    for (int i = 0; i < imagePixels.length; i++)
	    {
	        int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
	        int alpha = maskPixels[i] & 0xff000000; // Mask color values of the mask image
	        imagePixels[i] = color | alpha;
	    }

	    image.setRGB(0, 0, width, height, imagePixels, 0, width);
	}

	/**
	 * Adds a text to a copy of the given image. Position of the text can be
	 * influenced by <code>pos</code>. Text will be wrapped around the first
	 * space, if the text is to wide.
	 * 
	 * @param imgData
	 *            Image where the text should be added to.
	 * @param text
	 *            Text to be added to the image.
	 * @param pos
	 *            Position of the text (Top, Center, Bottom, see
	 *            {@link #TEXT_TOP}, {@link #TEXT_CENTER}, {@link #TEXT_BOTTOM})
	 * @return byte array with the image where the text was added
	 */
	public static SDImage addText(SDImage imgData, String text, int pos) {
		return addText(imgData, text, pos, DEFAULT_FONT.getSize());
	}

	/**
	 * Adds a text to a copy of the given image. Position of the text can be
	 * influenced by <code>pos</code>. Text will be wrapped around the first
	 * space, if the text is to wide.
	 * 
	 * @param imgData
	 *            Image where the text should be added to.
	 * @param text
	 *            Text to be added to the image.
	 * @param pos
	 *            Position of the text (Top, Center, Bottom, see
	 *            {@link #TEXT_TOP}, {@link #TEXT_CENTER}, {@link #TEXT_BOTTOM})
	 * @return byte array with the image where the text was added
	 */
	public static SDImage addText(BufferedImage imgData, String text, int pos) {
		return addText(imgData, text, pos, DEFAULT_FONT.getSize());
	}

	/**
	 * Adds a text to a copy of the given image. Position of the text can be
	 * influenced by <code>pos</code>. Text will be wrapped around the first
	 * space, if the text is to wide.
	 * 
	 * @param imgData
	 *            Image where the text should be added to.
	 * @param text
	 *            Text to be added to the image.
	 * @param pos
	 *            Position of the text (Top, Center, Bottom, see
	 *            {@link #TEXT_TOP}, {@link #TEXT_CENTER}, {@link #TEXT_BOTTOM})
	 * @param fontSize
	 *            Size of the font to use
	 * @return byte array with the image where the text was added
	 */
	public static SDImage addText(SDImage imgData, String text, int pos, float fontSize) {
		return addText(imgData.image, text, pos, fontSize);
	}
	
	/**
	 * Adds a text to a copy of the given image. Position of the text can be
	 * influenced by <code>pos</code>. Text will be wrapped around the first
	 * space, if the text is to wide.
	 * 
	 * @param imgData
	 *            Image where the text should be added to.
	 * @param text
	 *            Text to be added to the image.
	 * @param pos
	 *            Position of the text (Top, Center, Bottom, see
	 *            {@link #TEXT_TOP}, {@link #TEXT_CENTER}, {@link #TEXT_BOTTOM})
	 * @param fontSize
	 *            Size of the font to use
	 * @return byte array with the image where the text was added
	 */
	public static SDImage addText(BufferedImage imgData, String text, int pos, float fontSize) {
		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(imgData, 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(DEFAULT_FONT.deriveFont(Font.PLAIN, fontSize));
		int yStart = 28;
		switch (pos) {
		case TEXT_BOTTOM:
			yStart = 75;
			break;
		case TEXT_CENTER:
			yStart = 60;
			break;
		default:
			break;
		}
		List<String> lines = splitText(text, g2d.getFontMetrics());
		
		if (g2d.getFontMetrics().stringWidth(text) > 71 && text.contains(" ")) {
			text = text.replaceFirst(" ", "\n");
		}
		// Calculate y-offset for placing the text in the center, if applicable 
		int	yOffset = (int) (pos == TEXT_CENTER ? (lines.size() / 2.0) * g2d.getFontMetrics().getHeight() : 0);
		// Calculate y-offset for placing the text in the bottom, if applicable
		yOffset +=  (pos == TEXT_BOTTOM ? (lines.size() - 1) * g2d.getFontMetrics().getHeight() : 0);
		
		int y = (int) (yStart - (g2d.getFontMetrics().getHeight() / 2) - yOffset);
		
		g2d.setColor(new Color(0, 0, 0, textBoxAlphaValue));
		for (String line : lines) {
			int width = g2d.getFontMetrics().stringWidth(line);
			int x = (StreamDeck.ICON_SIZE / 2) - width / 2;
			g2d.fillRect(x - 1, y - g2d.getFontMetrics().getHeight() + 7, width + 2,
					g2d.getFontMetrics().getHeight() - 4);
			y += g2d.getFontMetrics().getHeight();
		}
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Calculate y-offset for placing the text in the center, if applicable 
		yOffset = (int) (pos == TEXT_CENTER ? (lines.size() / 2.0) * g2d.getFontMetrics().getHeight() : 0);
		// Calculate y-offset for placing the text in the bottom, if applicable
		yOffset +=  (pos == TEXT_BOTTOM ? (lines.size() - 1) * g2d.getFontMetrics().getHeight() : 0);
		
		y = (int) (yStart - (g2d.getFontMetrics().getHeight() / 2) - yOffset);
		
		for (String line : lines) {
			int width = g2d.getFontMetrics().stringWidth(line);
			int x = (StreamDeck.ICON_SIZE / 2) - width / 2;
			x = x < 0 ? 0 : x;
			g2d.drawString(line, x, y);
			y += g2d.getFontMetrics().getHeight();
		}
		g2d.dispose();
		return convertImage(img);
	}
	
	public static AnimationStack createRollingTextAnimation(SDImage imgData, String text, int pos) {
		return createRollingTextAnimation(imgData, text, pos, DEFAULT_FONT.getSize());
	}

	/**
	 * Creates an animation with running text
	 * 
	 * @param imgData
	 *            Image where the text should be added to.
	 * @param text
	 *            Text to be added to the image.
	 * @param pos
	 *            Position of the text (Top, Center, Bottom, see
	 *            {@link #TEXT_TOP}, {@link #TEXT_CENTER}, {@link #TEXT_BOTTOM})
	 * @param fontSize
	 *            Size of the font to use
	 * @return byte array with the image where the text was added
	 */
	public static AnimationStack createRollingTextAnimation(SDImage imgData, String text, int pos, float fontSize) {
		AnimationStack animation = new AnimationStack(AnimationStack.REPEAT_PING_PONG, true, AnimationStack.FRAME_RATE_30, AnimationStack.TRIGGER_AUTO, new SDImage[0]);
		BufferedImage baseImage = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, imgData.image.getType());
		BufferedImage drawOn = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, imgData.image.getType());
		List<SDImage> frames = new LinkedList<>();
		Graphics2D g2d = baseImage.createGraphics();
		g2d.drawImage(imgData.image, 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int yStart = 28;
		switch (pos) {
		case TEXT_BOTTOM:
			yStart = 75;
			break;
		case TEXT_CENTER:
			yStart = 60;
			break;
		default:
			break;
		}
		int y = (yStart - (g2d.getFontMetrics().getHeight() / 2));
		g2d.setColor(new Color(0, 0, 0, textBoxAlphaValue));
		int width = 72;
		int x = IconHelper.rollingTextPadding;
		g2d.fillRect(x - 1, y - g2d.getFontMetrics().getHeight() + 7, width + 2, g2d.getFontMetrics().getHeight() - 4);
		g2d.dispose();
		// Draw Frames
		g2d = drawOn.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(DEFAULT_FONT.deriveFont(Font.PLAIN, fontSize));
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawImage(baseImage, 0, 0, null);
		do {
			g2d.setFont(DEFAULT_FONT.deriveFont(Font.PLAIN, fontSize));
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawString(text, x, y);
			x--;
			BufferedImage frame = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, imgData.image.getType());
			Graphics2D g = frame.createGraphics();
			g.drawImage(drawOn, 0, 0, null);
			g.dispose();
			frames.add(convertImage(frame));
			g2d.drawImage(baseImage, 0, 0, null);
		} while(x+g2d.getFontMetrics().stringWidth(text) > StreamDeck.ICON_SIZE - IconHelper.rollingTextPadding);
		g2d.dispose();
		SDImage[] frameArray = frames.toArray(new SDImage[0]);
		animation.setFrames(frameArray);
		return animation;
	}

	/**
	 * Splits the text so no line is longer than an icon is wide
	 * @param text			Text to be split
	 * @param fontMetrics	Font Metric to be used for the split
	 * @return	List with lines(max 4)
	 */
	private static List<String> splitText(String text, FontMetrics fontMetrics) {
		int width = StreamDeck.ICON_SIZE;
		List<String> lines = new LinkedList<>();
		String[] arr = text.split(" ");
		int[] wordWidth = new int[arr.length];
		// Calculate the length of each word and save the length into the array
		// wordWidth
		for (int f = 0; f < wordWidth.length; f++) {
			char[] calcString = arr[f].toCharArray();
			int stringWidth = 0;
			for (int i = 0; i < calcString.length; i++) {
				if (i < calcString.length)
					stringWidth += fontMetrics.stringWidth(String.valueOf(calcString[i]));
			}
			wordWidth[f] = stringWidth;
		}
		String line = "";
		int lineWidth = 0;
		int spaceWidth = fontMetrics.stringWidth(" ");
		// Split paragraph into lines depending on Field width
		for (int i = 0; i < arr.length; i++) {
			if (lineWidth + (lineWidth > 0 ? spaceWidth : 0) + wordWidth[i] < width) {
				line += (!line.isEmpty() ? " " : "" ) + arr[i];
				lineWidth += (lineWidth > 0 ? spaceWidth : 0) + wordWidth[i];
			} else {
				if(!line.isEmpty() && (lines.size()+1)*fontMetrics.getHeight() <= StreamDeck.ICON_SIZE )
					lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
				line = (!line.isEmpty() ? " " : "" ) + arr[i];
				lineWidth = wordWidth[i];
			}
		}
		if (!line.isEmpty() && !line.equals(" ") && (lines.size()+1)*fontMetrics.getHeight() <= StreamDeck.ICON_SIZE) {
			lines.add(line.charAt(0) == ' ' ? line.substring(1) : line);
		}
		while (lines.size() > 4)
			lines.remove(lines.size()-1);
		return lines;
	}

	/**
	 * Caches the given image data with the path
	 * 
	 * @param path
	 *            Path for which the image data will be cached
	 * @param imgData
	 *            image data to be cached
	 */
	private static void cache(String path, SDImage imgData) {
		imageCache.put(path, imgData);
		logger.debug("Caching: " + path);
	}

	/**
	 * Converts given image to bgr color schema and caches the resulting image
	 * data.
	 * 
	 * @param path
	 *            Path to be caches
	 * @param img
	 *            Image to be cached, must be fo TYPE_INT_*RGB*
	 * @return Returns the cached image data
	 */
	public static SDImage cacheImage(String path, BufferedImage img) {
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE * 3];
		int imgDataCount = 0;
		// remove the alpha channel
		for (int i = 0; i < StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE; i++) {
			// RGB -> BGR
			imgData[imgDataCount++] = (byte) ((pixels[i] >> 16) & 0xFF);
			imgData[imgDataCount++] = (byte) (pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte) ((pixels[i] >> 8) & 0xFF);
		}
		SDImage sdImage = new SDImage(imgData, img);
		cache(path, sdImage);
		return sdImage;
	}

	/**
	 * Converts the given image to the stream deck format.<br>
	 * Format is:<br>
	 * Color Schema: BGR<br>
	 * Image size: 72 x 72 pixel<br>
	 * Stored in an array with each byte stored seperatly (Size of each array is
	 * 72 x 72 x 3 = 15_552).
	 * 
	 * @param img
	 *            Image to be converted
	 * @return Byte arraythat contains the given image, ready to be sent to the
	 *         stream deck
	 */
	public static SDImage convertImage(BufferedImage img) {
		return convertImage(img, applyFrame);
	}

	/**
	 * Converts the given image to the stream deck format.<br>
	 * Format is:<br>
	 * Color Schema: BGR<br>
	 * Image size: 72 x 72 pixel<br>
	 * Stored in an array with each byte stored seperatly (Size of each array is
	 * 72 x 72 x 3 = 15_552).
	 * 
	 * @param img
	 *            Image to be converted
	 * @return Byte arraythat contains the given image, ready to be sent to the
	 *         stream deck
	 */
	public static SDImage convertImage(BufferedImage img, boolean applyFrame) {
		BufferedImage sdImg = IconHelper.rotate180(IconHelper.createResizedCopy(IconHelper.fillBackground(img, Color.BLACK), false));
		BufferedImage imgSrc = IconHelper.createResizedCopy(IconHelper.fillBackground(img, Color.BLACK), true);
		if (applyFrame) {
			sdImg = applyFrame(sdImg);
			imgSrc = applyFrame(imgSrc);
		}
		int[] pixels = ((DataBufferInt) sdImg.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE * 3];
		int imgDataCount = 0;
		// remove the alpha channel
		for (int i = 0; i < StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE; i++) {
			// RGB -> BGR
			imgData[imgDataCount++] = (byte) ((pixels[i] >> 16) & 0xFF);
			imgData[imgDataCount++] = (byte) (pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte) ((pixels[i] >> 8) & 0xFF);
		}
		return new SDImage(imgData, imgSrc);
	}

	/**
	 * Applies a normal BufferedImage to an already BGR converted image in byte
	 * form
	 * 
	 * @param imgData
	 *            base image as byte array
	 * @param apply
	 *            image to be applied
	 */
	public static SDImage applyImage(SDImage imgData, BufferedImage apply) {

		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, 
				StreamDeck.ICON_SIZE, 
				imgData.
				image.
				getType());
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(imgData.image, null, 0, 0);
		g2d.drawImage(apply, null, 0, 0);
		g2d.dispose();
		return convertImage(img);
	}

	public static BufferedImage applyFrame(BufferedImage img) {
		BufferedImage nImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		if (FRAME != null) {
			Graphics2D g = nImg.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.drawImage(createColoredFrame(FRAME_COLOR).image, 0, 0, null);
			g.dispose();
		}
		return nImg;
	}

	/**
	 * Packs an icon and the associated animation frames and timing into one
	 * usable archive.
	 * 
	 * @param pathToArchive
	 *            Archive to be created
	 * @param pathToIcon
	 *            Icon that should normaly be displayed on one or multiple keys
	 *            on the stream deck.
	 * @param pathToGif
	 *            Gif, that
	 * @param stack
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void createIconPackage(String pathToArchive, String pathToIcon, String pathToGif,
			AnimationStack stack) throws URISyntaxException, IOException {
		Path path = Paths.get(pathToArchive);
		URI uri = new URI("jar", path.toUri().toString(), null);

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
			Path iconPath = fileSystem.getPath("icon.png");
			// save main icon
			Files.copy(Paths.get(pathToIcon), iconPath, StandardCopyOption.REPLACE_EXISTING);
			// save animation, if exists
			Path animationFile = fileSystem.getPath("animation.json");
			if (stack != null) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String text = gson.toJson(stack);
				Files.write(animationFile, text.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE, StandardOpenOption.CREATE);
				// save animation frames
				if (pathToGif != null && Files.exists(Paths.get(pathToGif))) {
					try {
						String[] imageatt = new String[] { "imageLeftPosition", "imageTopPosition", "imageWidth",
								"imageHeight" };

						ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
						ImageInputStream ciis = ImageIO.createImageInputStream(new File(pathToGif));
						reader.setInput(ciis, false);

						int noi = reader.getNumImages(true);
						BufferedImage master = null;

						for (int i = 0; i < noi; i++) {
							BufferedImage image = reader.read(i);
							IIOMetadata metadata = reader.getImageMetadata(i);

							Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
							NodeList children = tree.getChildNodes();

							for (int j = 0; j < children.getLength(); j++) {
								Node nodeItem = children.item(j);

								if (nodeItem.getNodeName().equals("ImageDescriptor")) {
									Map<String, Integer> imageAttr = new HashMap<String, Integer>();

									for (int k = 0; k < imageatt.length; k++) {
										NamedNodeMap attr = nodeItem.getAttributes();
										Node attnode = attr.getNamedItem(imageatt[k]);
										imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
									}
									if (i == 0) {
										master = new BufferedImage(imageAttr.get("imageWidth"),
												imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
									}
									master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"),
											imageAttr.get("imageTopPosition"), null);
								}
							}
							File tmpImgFile = File.createTempFile("edsc", ".png");
							ImageIO.write(master, "PNG", tmpImgFile);
							Path iconTargetPath = fileSystem.getPath(i + ".png");
							Files.copy(tmpImgFile.toPath(), iconTargetPath, StandardCopyOption.REPLACE_EXISTING);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			fileSystem.close();
		}
	}

	public static void createIconPackage(String pathToArchive, String pathToIcon, String[] pathToFrames,
			AnimationStack stack) throws URISyntaxException, IOException {
		Path path = Paths.get(pathToArchive);
		URI uri = new URI("jar", path.toUri().toString(), null);

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
			Path iconPath = fileSystem.getPath("icon.png");
			// save main icon
			Files.copy(Paths.get(pathToIcon), iconPath, StandardCopyOption.REPLACE_EXISTING);
			// save animation, if exists
			Path animationFile = fileSystem.getPath("animation.json");
			if (stack != null) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String text = gson.toJson(stack);
				Files.write(animationFile, text.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE);
				// save animation frames
				if (pathToFrames != null) {
					for (int i = 0; i < pathToFrames.length; i++) {
						if (pathToFrames[i] != null) {
							Path iconSourcePath = Paths.get(pathToFrames[i]);
							Path iconTargetPath = fileSystem.getPath(i + ".png");
							Files.copy(iconSourcePath, iconTargetPath, StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
			fileSystem.close();
		}
	}

	public static BufferedImage createResizedCopy(BufferedImage originalImage, boolean preserveType) {
		int scaledWidth = StreamDeck.ICON_SIZE;
		int scaledHeight = StreamDeck.ICON_SIZE;
		if (originalImage.getWidth() != originalImage.getHeight()) {
			float scalerWidth = ((float) StreamDeck.ICON_SIZE) / originalImage.getWidth();
			float scalerHeight = ((float) StreamDeck.ICON_SIZE) / originalImage.getWidth();
			if (scalerWidth < scaledHeight)
				scaledHeight = Math.round(scalerWidth * originalImage.getHeight());
			else
				scaledWidth = Math.round(scalerHeight * originalImage.getWidth());

		}
		int imageType = preserveType ? originalImage.getType() : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (true) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, (StreamDeck.ICON_SIZE - scaledWidth) / 2, (StreamDeck.ICON_SIZE - scaledHeight) / 2,
				scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}

	public static BufferedImage fillBackground(BufferedImage img, Color color) {
		BufferedImage nImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = nImg.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, nImg.getWidth(), nImg.getHeight());
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return nImg;
	}

	public static BufferedImage flipHoriz(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D gg = newImage.createGraphics();
		gg.drawImage(image, image.getHeight(), 0, -image.getWidth(), image.getHeight(), null);
		gg.dispose();
		return newImage;
	}

	public static SDImage getImage(String string) {
		if (imageCache == null)
			imageCache = new HashMap<>();
		return imageCache.get(string);
	}

	/**
	 * Loads an image from the jar file.
	 * 
	 * @param fileName Path to image.
	 * @return A BufferedImage with the image from the jar file or null, if the image could not be loaded.
	 */
	public static BufferedImage getImageFromResource(String fileName) {
		BufferedImage buff = null;
		try (InputStream inS = IconHelper.class.getResourceAsStream(fileName)) {
			if (inS != null) {
				logger.debug("Loading image as resource: " + fileName);
				buff = ImageIO.read(inS);
			} else {
				logger.error("Image does not exist: " + fileName);
				return null;
			}
		} catch (IOException e) {
			logger.error("Couldn't load image as resource: " + fileName);
			logger.error(e);
			e.printStackTrace();
			return null;
		}
		return buff;

	}

	private static Font loadFont(String resourcePath, int fontSize) {
		InputStream io = IconHelper.class.getResourceAsStream(resourcePath);
		try {
			return Font.createFont(Font.TRUETYPE_FONT, io).deriveFont(Font.PLAIN, fontSize);
		} catch (FontFormatException | IOException e) {
			return new JLabel().getFont().deriveFont(Font.PLAIN, fontSize);
		}
	}

	public static IconPackage loadIconPackage(String pathToZip) throws IOException, URISyntaxException {
		if (packageCache.containsKey(pathToZip))
			return packageCache.get(pathToZip);
		Path path = Paths.get(pathToZip);
		URI uri = new URI("jar", path.toUri().toString(), null);

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
			Path iconPath = fileSystem.getPath("icon.png");
			// load main icon
			SDImage icon = IconHelper.loadImage(iconPath);
			// load unmodified image
//			BufferedImage rawIcon = loadRawImage(iconPath);
			// load animation, if exists
			Path animFile = fileSystem.getPath("animation.json");
			// Raw Animation frames, if exists
			AnimationStack animation = null;
			if (Files.exists(animFile)) {
				String text = new String(Files.readAllBytes(animFile), StandardCharsets.UTF_8);
				Gson g = new Gson();
				animation = g.fromJson(text, AnimationStack.class);
				// Load animation frames
				int frameIndex = 0;
				Path frameFile = fileSystem.getPath((frameIndex++) + ".png");
				List<SDImage> frameList = new LinkedList<>();
				List<BufferedImage> rawFrameList = new LinkedList<>();
				while (Files.exists(frameFile)) {
					SDImage frame = IconHelper.loadImage(frameFile);
					frameList.add(frame);
					rawFrameList.add(loadRawImage(frameFile));
					frameFile = fileSystem.getPath((frameIndex++) + ".png");
				}
				SDImage[] frames = new SDImage[frameList.size()];
				for (int i = 0; i < frames.length; i++) {
					frames[i] = frameList.get(i);
				}
				animation.setFrames(frames);
			}
			fileSystem.close();
			IconPackage iconPackage = new IconPackage(icon, animation);
			packageCache.put(pathToZip, iconPackage);
			return iconPackage;
		}
	}

	public static SDImage loadImageSafe(String path) {
		return loadImageSafe(Paths.get(path != null ? path : "."));
	}

	public static SDImage loadImageSafe(Path path) {
		SDImage icon = null;
		try {
			icon = loadImage(path);
		} catch (Exception e) {
			icon = IconHelper.getImage(TEMP_BLACK_ICON);
		}
		return icon;
	}

	public static SDImage loadImage(Path path) throws IOException {
		if (imageCache.containsKey(path.getFileSystem().toString() + path.toAbsolutePath().toString()))
			return imageCache.get(path.getFileSystem().toString() + path.toAbsolutePath().toString());
		try (InputStream inputStream = Files.newInputStream(path)) {
			return loadImage(path.getFileSystem().toString() + path.toAbsolutePath().toString(), inputStream, false);
		}
	}

	public static SDImage loadImage(String path) throws IOException {
		if (imageCache.containsKey(path))
			return imageCache.get(path);
		FileInputStream fIn = new FileInputStream(new File(path));
		return loadImage(path, fIn, false);
	}

	public static SDImage loadImage(String path, InputStream inputStream, boolean disableCache) throws IOException {
		if (imageCache.containsKey(path) && !disableCache)
			return imageCache.get(path);
		BufferedImage img = ImageIO.read(inputStream);
		SDImage imgData = convertImage(img); 
		if(!disableCache)
			cache(path, imgData);
		return imgData;
	}

	public static BufferedImage loadRawImage(Path path) throws IOException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			return loadRawImage(path.getFileSystem().toString() + path.toAbsolutePath().toString(), inputStream);
		}
	}

	public static BufferedImage loadRawImage(String path, InputStream inputStream) throws IOException {
		BufferedImage img = ImageIO.read(inputStream);
		return img;
	}

	/**
	 * Loads and converts an image from the jar file ito na dispalyable Icon for the StreamDeck
	 * @param path	Path to icon
	 * @return	A SDImage containg a displayabe icon or null, if the image could not be loaded.
	 */
	public static SDImage loadImageFromResource(String path) {
		if (imageCache.containsKey(path))
			return imageCache.get(path);
		BufferedImage img = getImageFromResource(path);
		if (img != null) {
			SDImage imgData = convertImage(img); 
			cache(path, imgData);
			return imgData;
		}
		return null;
	}

	public static SDImage loadImageFromResourceSafe(String path) {
		if (imageCache.containsKey(path))
			return imageCache.get(path);
		BufferedImage img = getImageFromResource(path);
		if (img != null) {
			SDImage imgData = convertImage(img); 
			cache(path, imgData);
			return imgData;
		}
		return IconHelper.getImage(TEMP_BLACK_ICON);
	}

	public static SDImage[] loadImagesFromGif(String pathToGif) {
		try {
			String[] imageatt = new String[] { "imageLeftPosition", "imageTopPosition", "imageWidth", "imageHeight" };

			ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
			ImageInputStream ciis = ImageIO.createImageInputStream(new File(pathToGif));
			reader.setInput(ciis, false);

			int noi = reader.getNumImages(true);
			BufferedImage master = null;

			SDImage[] images = new SDImage[noi];

			for (int i = 0; i < noi; i++) {
				BufferedImage image = reader.read(i);
				IIOMetadata metadata = reader.getImageMetadata(i);

				Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
				NodeList children = tree.getChildNodes();

				for (int j = 0; j < children.getLength(); j++) {
					Node nodeItem = children.item(j);

					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						Map<String, Integer> imageAttr = new HashMap<String, Integer>();

						for (int k = 0; k < imageatt.length; k++) {
							NamedNodeMap attr = nodeItem.getAttributes();
							Node attnode = attr.getNamedItem(imageatt[k]);
							imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
						}
						if (i == 0) {
							master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"),
									BufferedImage.TYPE_INT_ARGB);
						}
						master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"),
								imageAttr.get("imageTopPosition"), null);
					}
				}
				// ImageIO.write(master, "GIF", new File( i + ".gif"));
				SDImage imgData = convertImage(master); 
				images[i] = imgData;
			}
			return images;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
				int nX = height - x - 1;
				int nY = height - (width - y - 1) - 1;
				returnImage.setRGB(nX, nY, inputImage.getRGB(x, y));
			}
		}
		return returnImage;
	}
	
	private IconHelper() {}
}