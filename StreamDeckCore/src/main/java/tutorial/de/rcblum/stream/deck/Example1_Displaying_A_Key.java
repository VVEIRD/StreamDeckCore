package tutorial.de.rcblum.stream.deck;

import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import test.de.rcblum.stream.deck.TestAnimationStack;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

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
		//The image data is already in the necessary format to be delivered to the ESD. The icons will be
		// cached in memory by IconHelper to reduce load times when on icon is used multiple times.

		// Load the image for the touch screen of the Stream Deck Plus
		SDImage touchScreen = IconHelper.loadImage("resources" + File.separator + "lcd" + File.separator + "fantasy_background.png");

		//Send the image data to the first key of the ESD:
		streamDeck.drawImage(0,  iconData);
		if(streamDeck.hasTouchScreen()) {
			streamDeck.getTouchScreen().drawTouchScreen(touchScreen);
		}
		for (int i = 0; i < 800; i++) {
			streamDeck.drawTouchScreenImage(new Point(i, 0),  touchScreen);
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		streamDeck.drawTouchScreenImage(new Point(0, 0),  touchScreen);
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
