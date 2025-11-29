package de.rcblum.stream.deck.device.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.StreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;

/**
 * Dispatcher that asynchronously sends out all issued {@link KeyEvent}s.
 * @author Roland von Werden
 *
 */
public class EventDispatcher implements Runnable {
	
	private static final Logger LOGGER = LogManager.getLogger(EventDispatcher.class);
	
	final StreamDeck streamDeck;
	
	public EventDispatcher(StreamDeck streamDeck) {
		this.streamDeck = streamDeck;
	}

	@Override
	public void run() {
		while (streamDeck.isRunning() || !streamDeck.isRunning() && !streamDeck.isRecievePoolEmpty()) {
			if (!streamDeck.isRecievePoolEmpty()) {
				KeyEvent event = streamDeck.pollRecievePool();
				if (event == null)
					continue;
				int i = event.getKeyId();
				if (i < this.streamDeck.getKeySize() && this.streamDeck.getKey(i) != null) {
					this.streamDeck.getKey(i).onKeyEvent(event);
				}
				this.streamDeck.getListeners().stream().forEach(l -> 
					{
						try {
							l.onKeyEvent(event);
						} 
						catch (Exception e) {
							LOGGER.error("Error sending out KeyEvents", e);
						}
					}
				);
			}
			if (streamDeck.isRecievePoolEmpty()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					LOGGER.error("EventDispatcher sleep interrupted", e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
