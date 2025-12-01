package de.rcblum.stream.deck.device.components;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;

public class DialKey implements StreamKeyListener {
	
	private static final Logger LOGGER = LogManager.getLogger(DialKey.class);
	/**
	 * Id of the dial within the stream deck
	 */
	private final int keyId;

	private final IStreamDeck streamDeck;
	
	private java.util.List<StreamKeyListener> listeners = new CopyOnWriteArrayList<StreamKeyListener>();
	
	private KeyEvent lastEvent = null;
	

	public DialKey(int keyId, IStreamDeck streamDeck) {
		super();
		this.keyId = keyId;
		this.streamDeck = streamDeck;
	}

	public boolean isPressed() {
		return this.lastEvent != null && this.lastEvent.getType() == KeyEvent.Type.PRESSED;
	}
	
	public boolean addListener(StreamKeyListener listener) {
		return this.listeners.add(listener);
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
		if (event.getKeyId() == this.keyId) {
			this.lastEvent = event;
			for (Iterator<StreamKeyListener> iterator = listeners.iterator(); iterator.hasNext();) {
				try {
					iterator.next().onKeyEvent(event);
				}
				catch(Exception e) {
					LOGGER.error("Error processing key event on listener");
					LOGGER.error(e);
				}
			}
		}
	}

}
