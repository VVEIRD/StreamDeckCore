package tutorial.de.rcblum.stream.deck;

import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;
import test.de.rcblum.stream.deck.TestAnimationStack;

import java.io.IOException;

public class Example2_Receiving_key_events {
	public static void main(String[] args) throws IOException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		// Get the first connected (or software) ESD:
		IStreamDeck streamDeck = StreamDeckDevices.getStreamDeck();
		// Reset the ESD so we can display our icon on it:
		streamDeck.reset();
		// Set the brightness to 75%
		streamDeck.setBrightness(50);
		// Add the Listener to the stream deck:
		streamDeck.addKeyListener(new ExampleListener());
		// Wait 30 seconds before shutting down
		try {
			Thread.sleep(60_000);
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
	
	public static class ExampleListener implements StreamKeyListener {
		public void onKeyEvent(KeyEvent event) {
			switch(event.getType()) {
			case OFF_DISPLAY :
				System.out.println(event.getKeyId() + ": taken off display");
				break;
			case ON_DISPLAY:
				System.out.println(event.getKeyId() + ": put on display");
				break;
			case PRESSED:
				System.out.println(event.getKeyId() + ": pressed");
				break;
			case RELEASED_CLICKED:
				System.out.println(event.getKeyId() + ": released/clicked");
				break;
			case ROTATE_LEFT:
				System.out.println(event.getKeyId() + ": rotated left, amount: " + event.getNewValue());
				break;
			case ROTATE_RIGHT:
				System.out.println(event.getKeyId() + ": rotated right, amount: " + event.getNewValue());
				break;
			case TOUCHED:
				System.out.println(event.getKeyId() + ": touched, Point: " + event.getNewValue());
				break;
			case TOUCHED_LONG:
				System.out.println(event.getKeyId() + ": touched long, Point: " + event.getNewValue());
				break;
			case SWIPE_LEFT:
				System.out.println(event.getKeyId() + ": swiped left, From: " + event.getOldValue() + " To: " + event.getNewValue());
				break;
			case SWIPE_RIGHT:
				System.out.println(event.getKeyId() + ": swiped right, From: " + event.getOldValue() + " To: " + event.getNewValue());
				break;
			case SWIPE_DOWN:
				System.out.println(event.getKeyId() + ": swiped down, From: " + event.getOldValue() + " To: " + event.getNewValue());
				break;
			case SWIPE_UP:
				System.out.println(event.getKeyId() + ": swiped up, From: " + event.getOldValue() + " To: " + event.getNewValue());
				break;
			case SWIPED:
				System.out.println(event.getKeyId() + ": swiped, From: " + event.getOldValue() + " To: " + event.getNewValue());
				break;
			default:
				break;
			}
		}
	}
}
