package de.rcblum.stream.deck.device.components;

import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.items.animation.Animator;
import de.rcblum.stream.deck.items.listeners.AnimationListener;
import de.rcblum.stream.deck.util.SDImage;

public class TouchScreen implements StreamKeyListener, AnimationListener {
	
	private static final Logger LOGGER = LogManager.getLogger(TouchScreen.class);
	/**
	 * Id of the dial within the stream deck
	 */
	private final int keyId;
	
	private SDImage image = null;
	
	private Animator animator = null;

	private final IStreamDeck streamDeck;
	
	private java.util.List<StreamKeyListener> listeners = new CopyOnWriteArrayList<StreamKeyListener>();
	
	private KeyEvent lastEvent = null;
	
	private List<KeyEvent.Type> allowedEvents = Arrays.asList(new KeyEvent.Type[]  {
			KeyEvent.Type.TOUCHED, KeyEvent.Type.TOUCHED_LONG, KeyEvent.Type.SWIPE_LEFT, KeyEvent.Type.SWIPE_RIGHT, KeyEvent.Type.SWIPE_DOWN, KeyEvent.Type.SWIPE_UP, KeyEvent.Type.SWIPED
	});
	

	public TouchScreen(int keyId, IStreamDeck streamDeck) {
		super();
		this.keyId = keyId;
		this.streamDeck = streamDeck;
	}
	
	public boolean addListener(StreamKeyListener listener) {
		return this.listeners.add(listener);
	}
	
	public StreamKeyListener removeListener(StreamKeyListener listener) {
		StreamKeyListener remove = null;
		if (this.listeners.indexOf(listener) >= 0) {
			remove = this.listeners.get(this.listeners.indexOf(listener));
			this.listeners.remove(this.listeners.indexOf(listener));
		}
		return remove;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
		if (event.getKeyId() == this.keyId && allowedEvents.contains(event.getType())) {
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
			try {
				if (this.animator != null)
					this.animator.onKeyEvent(event);
			}
			catch(Exception e) {
				LOGGER.error("Error processing key event on listener");
				LOGGER.error(e);
			}
		}
	}
	
	public boolean removeTouchScreenAnimation() {
		if (this.animator != null) {
			this.animator.stop(true);
			this.animator = null;
			return true;
		}
		return false;
	}
	
	public void addTouchScreenAnimation(AnimationStack as) {
		if (this.streamDeck.hasTouchScreen()){
			if (this.animator != null) {
				this.animator.stop(true);
			}
			this.animator = new Animator(this.streamDeck, this.keyId, as);
		}
	}
	
	public void drawTouchScreen(SDImage image) {
		if (this.streamDeck.hasTouchScreen()) {
			this.image = image;
			this.streamDeck.drawTouchScreenImage(image);
		}
	}
	
	public void drawTouchScreenSection(Point coordinates, SDImage image) {
		if (this.streamDeck.hasTouchScreen())
			this.streamDeck.drawTouchScreenImage(coordinates, image);
	}

	@Override
	public void onAnimationStart(int keyIndex) {
		
	}

	@Override
	public void onAnimationStop(int keyIndex) {
		if (this.keyId == keyIndex && this.image != null)
			this.drawTouchScreen(this.image);
	}

}
