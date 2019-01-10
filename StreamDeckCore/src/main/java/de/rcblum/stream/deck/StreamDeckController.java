package de.rcblum.stream.deck;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.IStreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.items.animation.Animator;
import de.rcblum.stream.deck.items.listeners.AnimationListener;
import de.rcblum.stream.deck.items.listeners.IconUpdateListener;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;

/**
 * Can be used to hand over control over the stream deck by providing a "folder"
 * structure. Folders will be traversed automatically, KeyEvents will be
 * forwarded to non folder {@link StreamItem}s.
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
public class StreamDeckController implements StreamKeyListener, IconUpdateListener, AnimationListener {
	
	private static final Logger LOGGER = LogManager.getLogger(StreamDeckController.class);

	/**
	 * Sets the key dead zone for key events, default 25 ms.
	 */
	private static long KEY_DEAD_ZONE = 25;

	/**
	 * Sets the key dead zone. The dead zone defines how much time in milliseconds
	 * after a Key released event must have passed before another will be forwarded.
	 * 
	 * @param keyDeadZone Time in MS between key released events
	 */
	public static void setKeyDeadzone(long keyDeadZone) {
		KEY_DEAD_ZONE = keyDeadZone;
	}

	/**
	 * Back icon with an arrow, displayed on the top left button when entering a
	 * folder
	 */
	private SDImage back = null;

	/**
	 * Time, the last time a key was releaed, is used for the key dead zone
	 */
	private long lastKeyReleasedEvent = System.currentTimeMillis();

	/**
	 * Proxy for interacting with the ESD
	 */
	private IStreamDeck streamDeck = null;

	/**
	 * Root folder with the initial items to be dispalyed
	 */
	private StreamItem root = null;

	/**
	 * Currently dispalyed folder
	 */
	private StreamItem currentDir = null;

	/**
	 * Animators to the currently displayed items. <code>null</code> if no animation
	 * is present.
	 */
	private Animator[] animators = null;

	/**
	 * Creates the StreamDeckController with a root folder and the ESD
	 * 
	 * @param streamDeck
	 * @param root
	 */
	public StreamDeckController(IStreamDeck streamDeck, StreamItem root) {
		super();
		this.back = IconHelper.loadImageFromResource("/resources/icons/back.png");
		if (this.back == null)
			this.back = IconHelper.addText(IconHelper.getImage("temp://FOLDER"), "back", StreamItem.TEXT_POS_BOTTOM);
		this.streamDeck = streamDeck;
		this.streamDeck.addKeyListener(this);
		this.root = root;
		while (this.root.getParent() != null)
			this.root = this.root.getParent();
		this.currentDir = root;
		this.animators = new Animator[this.streamDeck.getKeySize()];
		this.updateDisplay();
		this.addIconListener();
		this.fireOnDisplay();
		// Sleep the creation of the SDC for 200 ms so it wont break due to fast inputs.
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			LOGGER.error("Thread was interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Fire an off-display event to the currently displayed items, their animators
	 * and an close folder event to their containing folder.
	 */
	private void fireOffDisplay() {
		if (this.currentDir != null) {
			if (!this.currentDir.isLeaf()) {
				StreamItem[] children = this.currentDir.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] != null) {
						KeyEvent evnt = new KeyEvent(this.streamDeck, i, Type.OFF_DISPLAY);
						children[i].onKeyEvent(evnt);
						if (this.animators[i] != null) {
							this.animators[i].onKeyEvent(evnt);
						}
					}
				}
			}
			KeyEvent evnt = new KeyEvent(streamDeck, -1, Type.CLOSE_FOLDER);
			this.currentDir.onKeyEvent(evnt);
		}
	}

	/**
	 * Fire an open folder event to the current folder. Fire an off-display event to
	 * the currently displayed items and their animators.
	 */
	private void fireOnDisplay() {
		if (this.currentDir != null) {
			KeyEvent folderEvnt = new KeyEvent(streamDeck, -1, Type.OPEN_FOLDER);
			this.currentDir.onKeyEvent(folderEvnt);
		
			StreamItem[] children = this.currentDir.getChildren();
			if (children != null)
				for (int i = 0; i < children.length; i++) {
					if (children[i] != null) {
						KeyEvent evnt = new KeyEvent(this.streamDeck, i, Type.ON_DISPLAY);
						children[i].onKeyEvent(evnt);
						if (this.animators[i] != null) {
							this.animators[i].onKeyEvent(evnt);
						}
					}
				}
		}
	}
	
	/**
	 * returns the StreamDeck used by the controller.
	 * @return
	 */
	public IStreamDeck getStreamDeck() {
		return streamDeck;
	}

	/**
	 * Handling key events from the ESD.
	 * 
	 * @param event Key event to be processed by the SDC.
	 */
	@Override
	public void onKeyEvent(KeyEvent event) {
		this.onKeyEvent(event, true);
	}

	/**
	 * Handling key events from the ESD.
	 * 
	 * @param event       Event to be processed
	 * @param useDeadzone <code>true</code> if deadzone should be used,
	 *                    <code>false</code> if the deadzone should be ignored
	 */
	private synchronized void onKeyEvent(KeyEvent event, boolean useDeadzone) {
		LOGGER.debug(String.format("New Key event: Key-ID: %s, Type: %s", event.getKeyId(), event.getType()));
		if (event.getType() == Type.RELEASED_CLICKED
				&& System.currentTimeMillis() - lastKeyReleasedEvent < KEY_DEAD_ZONE && useDeadzone)
			return;
		StreamItem[] children = this.currentDir.getChildren();
		int id = event.getKeyId();
		Type type = event.getType();
		if (id == 4 && this.currentDir.getParent() != null && type == Type.RELEASED_CLICKED) {
			openFolder(this.currentDir.getParent());
		} else if (children[id] != null && !children[id].isLeaf() && type == Type.RELEASED_CLICKED) {
			openFolder(children[id]);
		} else if (children[id] != null && !(id == 4 && this.currentDir.getParent() != null)) {
			children[id].onKeyEvent(event);
			if (this.animators[id] != null) {
				this.animators[id].onKeyEvent(event);
			}
		}
		lastKeyReleasedEvent = System.currentTimeMillis();
	}

	/**
	 * Manually Pushing a button at the given id.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void pushButton(int no) {
		LOGGER.debug(String.format("Virtual button pushed: Key-ID: %d", no));
		no = no > 14 ? 14 : no < 0 ? 0 : no;
		KeyEvent evnt = new KeyEvent(streamDeck, no, Type.RELEASED_CLICKED);
		this.onKeyEvent(evnt, false);
	}

	/**
	 * Manually presses a button at the given id until {@link #releaseButton(int)}
	 * is called.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void pressButton(int no) {
		LOGGER.debug(String.format("Virtual button pressed: Key-ID: %d", no));
		no = no > 14 ? 14 : no < 0 ? 0 : no;
		KeyEvent evnt = new KeyEvent(streamDeck, no, Type.PRESSED);
		this.onKeyEvent(evnt, false);
	}

	/**
	 * Manually releases a button at the given id. If the button is not pressed, it
	 * will be pushed instead.
	 * 
	 * @param no Number of the button to be pushed, 0 - 14, right top to left
	 *           bottom.
	 */
	public void releaseButton(int no) {
		LOGGER.debug(String.format("Virtual button released: Key-ID: %d", no));
		this.pushButton(no);
	}

	/**
	 * Updates the display data.<br>
	 * <br>
	 * BUGFIXES:<br>
	 * #1 - Changed if query to work with proxy items (skeletons encasing the real
	 * item(s))
	 */
	@Override
	public void onIconUpdate(StreamItem source, boolean animationChanged) {
		LOGGER.debug("Icon on child changed: " + source);
		if (this.currentDir != null && this.currentDir.getChildId(source) >= 0) {
			StreamItem[] children = this.currentDir.getChildren();
			for (int i=0; i<children.length; i++) {
				StreamItem child = children[i];
				if (child == source) {
					LOGGER.debug("Updating key " + i);
					this.updateKey(i, animationChanged);
				}
			}
		}
	}

	/**
	 * Opens the given folder item.
	 * 
	 * @param folder Folder to be displayed on the StreamDeck.
	 */
	private void openFolder(StreamItem folder) {
		folder = Objects.requireNonNull(folder);
		if (!folder.isLeaf() && this.currentDir != folder) {
			this.fireOffDisplay();
			this.removeIconListener();
			this.currentDir = folder;
			this.addIconListener();
			this.updateDisplay();
			this.fireOnDisplay();
		}
	}
	
	/**
	 * Sets a new root folder and opens it
	 * @param root New root folder
	 */
	public void setRoot(StreamItem root) {
		LOGGER.debug(String.format("New root folder for %s", this.streamDeck.getHidDevice() != null ? this.streamDeck.getHidDevice().getHidDeviceInfo().getProductString() : "SoftStreamDeck"));
		this.root = root;
		while (this.root.getParent() != null)
			this.root = this.root.getParent();
		openFolder(root);
	}

	/**
	 * Removes the {@link StreamDeckController} from all cildren of the current
	 * directory.
	 */
	private void removeIconListener() {
		if(this.currentDir != null && !this.currentDir.isLeaf()) {
			StreamItem[] children = this.currentDir.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					children[i].removeIconUpdateListener(this);
				}
			}
		}
	}

	/**
	 * Adds this instance as IconListener to the children of the current folder.
	 */
	private void addIconListener() {
		if(this.currentDir != null && !this.currentDir.isLeaf()) {
			StreamItem[] children = this.currentDir.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					children[i].addIconUpdateListener(this);
				}
			}
		}
	}

	/**
	 * Updates the stream deck with current items, updates animators
	 */
	private void updateDisplay() {
		this.updateDisplay(true);
	}

	/**
	 * Updates the stream deck with current
	 */
	private void updateDisplay(boolean updateAnimators) {
		StreamItem[] children = this.currentDir.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				this.updateKey(i, updateAnimators);
			}
		}
	}

	/**
	 * Updates a key on the stream deck with current icon data
	 */
	private void updateKey(int keyId, boolean updateAnimators) {
		StreamItem[] children = this.currentDir.getChildren();
		if (children != null) {
			int i = keyId;
			if (updateAnimators && this.animators[i] != null) {
				this.animators[i].removeAnimationListener(this);
				this.animators[i].stop(true);
				this.animators[i] = null;
			}
			if (this.currentDir.getParent() != null && i == 4) {
				streamDeck.drawImage(i, this.back);
			} else if (children[i] != null && (this.animators[i] == null || !this.animators[i].isActive())) {
				if (this.animators[i] != null && this.animators[i].isActive()) {
					streamDeck.drawImage(i, this.animators[i].getCurrentIcon());
				} else {
					streamDeck.drawImage(i, children[i].getIcon());
				}
				if (updateAnimators && children[i].hasAnimation()) {
					Animator a = new Animator(streamDeck, i, children[i].getAnimation());
					this.animators[i] = a;
					this.animators[i].addAnimationListener(this);
				}
			} else {
				streamDeck.clearButton(i);
			}
		}
	}

	/**
	 * Wrapper to set brightness of stream deck
	 * 
	 * @param brightness Brightness in percent 0 - 100 %
	 */
	public void setBrightness(int brightness) {
		this.streamDeck.setBrightness(brightness);
	}

	/**
	 * Resets stream deck and updates display of keys
	 */
	public void resetStreamDeck() {
		this.streamDeck.reset();
		this.updateDisplay();
	}

	/**
	 * Stops the update by animators on the streamdeck and the streamdeck itself
	 * 
	 * @param immediate <code>true</code> = Stop all updating at once,
	 *                  <code>false</code> = stop after animation is done
	 */
	public void stop(boolean immediate, boolean shutdownStreamDeck) {
		for (int i = 0; i < animators.length; i++) {
			if (animators[i] != null) {
				animators[i].stop(immediate);
			}
		}
		this.fireOffDisplay();
		this.removeIconListener();
		this.streamDeck.removeKeyListener(this);
		if(shutdownStreamDeck) {
			this.streamDeck.reset();
			this.streamDeck.setBrightness(0);
			this.streamDeck.stop();
			this.streamDeck.waitForCompletion();
		}
			
	}

	@Override
	public void onAnimationStart(int keyIndex) {
	}

	@Override
	public void onAnimationStop(int keyIndex) {
		if (this.currentDir != null && this.currentDir.getChildren() != null && keyIndex >= 0
				&& keyIndex < this.currentDir.getChildren().length && this.currentDir.getChildren()[keyIndex] != null) {
			this.streamDeck.drawImage(keyIndex, this.currentDir.getChildren()[keyIndex].getIcon());
		}
	}

}
