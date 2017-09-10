package de.rcblum.stream.deck.items.animation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.rcblum.stream.deck.StreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;

/**
 * Controller that handles the animation of a key.
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
 * @version 0.1
 *
 */
public class Animator implements StreamKeyListener, Runnable {

	/**
	 * Stream Deck that conatins the key on which the animation should be
	 * displayed
	 */
	private StreamDeck streamDeck = null;

	/**
	 * Key on above stream deck on which the animation should be displayed
	 */
	private int keyIndex = -1;

	/**
	 * Animation Metadata and frames used for animation
	 */
	private AnimationStack animation = null;

	/**
	 * Frame position on currently played animation
	 */
	private int framePos = 0;

	/**
	 * Direction on which fram should be used next. When using
	 * {@link AnimationStack#REPEAT_PING_PONG} this will be inverted, wehn the
	 * end of the frames is reached.
	 */
	private int frameAdvance = 1;

	/**
	 * Scheduler, that delivers the frames to the stream deck in the frame rate
	 * given by {@link Animator#animation}
	 */
	private ScheduledExecutorService scheduler = null;

	/**
	 * Flag to indicate, that the animation should be stopped afuter the
	 * animation finished. This will never happen if the animation is set to
	 * loop or ping pong atm.
	 */
	private boolean stopAfterAnimation = false;

	/**
	 * Creates an animator for the given stream deck key and
	 * {@link AnimationStack}
	 * 
	 * @param streamDeck
	 *            Stream Deck with the key
	 * @param keyIndex
	 *            Index of the key
	 * @param animation
	 *            {@link AnimationStack} that contains framrate trigger loop
	 *            behavior and frames of the animation
	 */
	public Animator(StreamDeck streamDeck, int keyIndex, AnimationStack animation) {
		System.out.println("New animator: " + keyIndex);
		this.streamDeck = streamDeck;
		this.keyIndex = keyIndex;
		this.animation = animation;
		System.out.println("Autoplay: " + this.animation.autoPlay());
		if (this.animation.autoPlay())
			this.start();
	}

	/**
	 * Checks if the key event should start the animation
	 */
	public void onKeyEvent(KeyEvent event) {
		boolean triggered = this.animation.isTriggered(event.getType());
		if (triggered && event.getKeyId() == keyIndex && this.scheduler == null) {
			this.start();
		} else if (!triggered && event.getKeyId() == keyIndex && this.scheduler != null) {
			this.stop(false);
		}
	}

	public void start() {
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.scheduler.scheduleAtFixedRate(this, 1_000_000 / this.animation.getFrameRate(),
				1_000_000 / this.animation.getFrameRate(), TimeUnit.MICROSECONDS);
	}

	public void stop(boolean immediate) {
		if (this.scheduler != null) {
			if (immediate) {
				this.scheduler.shutdown();
				this.scheduler = null;
				this.framePos = 0;
				this.frameAdvance = 1;
				this.stopAfterAnimation = false;
			} else {
				this.stopAfterAnimation = true;
			}
		}
	}

	public void run() {
		byte[] frame = null;
		int frameCount = this.animation.getFrameCount();
		// Get nexte frame to render
		if (this.framePos >= 0 && this.framePos < frameCount) {
			frame = this.animation.getFrame(framePos);
		}
		// Draw frame
		this.streamDeck.drawImage(this.keyIndex, frame);
		// Handle normal frame advance
		if (this.framePos + this.frameAdvance >= 0 && this.framePos + this.frameAdvance < frameCount) {
			this.framePos += this.frameAdvance;
		}
		// Handle Loops
		else if (this.framePos + this.frameAdvance >= frameCount && this.animation.loop()) {
			this.framePos = 0;
		}
		// Handle Ping Pong
		else if ((this.framePos + this.frameAdvance >= frameCount || framePos + this.frameAdvance < 0)
				&& this.animation.pingPong()) {
			this.frameAdvance = -1 * this.frameAdvance;
			this.framePos += this.frameAdvance;
		}
		// Handle Play once
		else if (this.framePos + this.frameAdvance >= frameCount && this.animation.playOnce()
				|| this.stopAfterAnimation) {
			this.scheduler.shutdown();
			this.scheduler = null;
			this.framePos = 0;
			this.frameAdvance = 1;
			this.stopAfterAnimation = false;
		}
	}

	public boolean isActive() {
		return this.scheduler != null && !this.scheduler.isShutdown();
	}
}