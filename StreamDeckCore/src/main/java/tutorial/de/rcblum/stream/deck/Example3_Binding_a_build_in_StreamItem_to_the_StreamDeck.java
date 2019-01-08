package tutorial.de.rcblum.stream.deck;

import de.rcblum.stream.deck.device.IStreamDeck;
import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.RunnableItem;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;

import java.io.File;
import java.io.IOException;

public class Example3_Binding_a_build_in_StreamItem_to_the_StreamDeck {
	public static void main(String[] args) throws IOException {
		// Create the ExecutableItem with the icon "icon1.png":
		SDImage img1 = IconHelper.loadImage("resources" + File.separator + "icon1.png");
		ExecutableItem executableItem = new ExecutableItem(img1,"explorer.exe");
		// Create the RunnableItem with the icon "icon2.png":
		SDImage img2 = IconHelper.loadImage("resources" + File.separator + "icon2.png");
		RunnableItem runnableItem = new RunnableItem(img2,
			new Runnable() {
				public void run() {
					System.out.println("Runnable task has been called.");
				}
			}
		);
		// Now bind the two items to the 6th and 10th key on the StreamDeck:
		// Get the first connected (or software) ESD:
		IStreamDeck streamDeck = StreamDeckDevices.getStreamDeck();
		// Reset the ESD so we can display our icon on it:
		streamDeck.reset();
		// Set the brightness to 75%
		streamDeck.setBrightness(75);
		// Add the executable item to the 6th key (First key from the right of the second row)
		streamDeck.addKey(5, executableItem);
		// Add the runnable item to the 10th key (Last key from the right of the second row)
		streamDeck.addKey(9, runnableItem);
		// Wait 30 seconds before shutting down
		try {
			Thread.sleep(30_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
