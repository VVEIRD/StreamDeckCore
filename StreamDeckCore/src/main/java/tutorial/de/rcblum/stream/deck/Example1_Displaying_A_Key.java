package tutorial.de.rcblum.stream.deck;

import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import test.de.rcblum.stream.deck.TestAnimationStack;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Example1_Displaying_A_Key {
	public static void main(String[] args) throws IOException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		// Get the first connected (or software) ESD:
		IStreamDeck streamDeck = StreamDeckDevices.getStreamDeck();
		// Reset the ESD so we can display our icon on it:
		streamDeck.reset();
		// Set the brightness to 75%
		streamDeck.setBrightness(75);
		// Load the image "resources/icon.png" from disk:
		SDImage iconData = IconHelper.loadImage("resources" + File.separator + "icon.png");
		BufferedImage backgroundBI = IconHelper.createResizedCopy(IconHelper.loadRawImage(Paths.get("resources" + File.separator + "caibrate_stream_deck_plus.png")), false, new Dimension(800, 500));
		SDImage background = IconHelper.cacheImage("resources" + File.separator + "caibrate_stream_deck_plus.png", backgroundBI);
		//The image data is already in the necessary format to be delivered to the ESD. The icons will be
		// cached in memory by IconHelper to reduce load times when on icon is used multiple times.

		//If the image is already available as BufferedImage, the BufferedImage can be converted to the ESD format:
		// SDImage iconData = IconHelper.convertImage(bufferedImage);

		//Send the image data to the first key of the ESD:
		streamDeck.drawImage(0,  iconData);
		streamDeck.drawFullImage(background);
		File outputFile = new File("/home/owl/Downloads/test.jpg");
		try (FileOutputStream fout = new FileOutputStream(outputFile)) {
			fout.write(iconData.getVariant(new Dimension(800,800)).sdImageJpeg);
		}
		try {
			Thread.sleep(15_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		// Reset the stream deck to display nothing
		streamDeck.reset();
		// Set the brightness to 0%
		streamDeck.setBrightness(0);
		// Tell the device to shutdown
		streamDeck.stop();
		// wait for the device to shutdown
		streamDeck.waitForCompletion();
	}
}
