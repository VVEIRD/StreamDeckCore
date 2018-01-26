package de.rcblum.stream.deck.items.animation;

import java.util.Objects;

import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;

/**
 * Structure that contains all necessary data to display an animation on a
 * key.<br>
 * See {@link Animator} for actually animating on a stream deck key.
 * 
 * <br>
 * <br>
 * 
 * MIT License<br>
 * <br>
 * Copyright (c) 2017 Roland von Werden<br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy<br>
 * of this software and associated documentation files (the "Software"), to deal<br>
 * in the Software without restriction, including without limitation the rights<br>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>
 * copies of the Software, and to permit persons to whom the Software is<br>
 * furnished to do so, subject to the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be included in all<br>
 * copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE<br>
 * SOFTWARE.<br>
 * 
 * @author Roland von Werden
 * @version 0.1
 *
 */
public class AnimationStack {

	/**
	 * Animation should be looped when reaching the last frame
	 */
	public static final int REPEAT_LOOPING = 0;

	/**
	 * Animation should stop when reaching the last frame.
	 */
	public static final int REPEAT_ONCE = 1;

	/**
	 * Animation should be played in reverse when reaching on end of the frames.
	 */
	public static final int REPEAT_PING_PONG = 2;

	/**
	 * Animation should be played at the frame rate of 15 frames per second.
	 */
	public static final int FRAME_RATE_15 = 15;

	/**
	 * Animation should be played at the frame rate of 30 frames per second.
	 */
	public static final int FRAME_RATE_30 = 30;

	/**
	 * Animation should be played at the frame rate of 60 frames per second.
	 */
	public static final int FRAME_RATE_60 = 60;

	/**
	 * The animation should be playeed as soon as the accosiated item is
	 * displayed on a key.
	 */
	public static final int TRIGGER_AUTO = 0;

	/**
	 * The animation should be played when the associated key is pressed down
	 */
	public static final int TRIGGER_PRESSED = 1;

	/**
	 * The animation should be played when the associated key is released
	 */
	public static final int TRIGGER_CLICKED = 2;

	/**
	 * Trigger when the animation should be played
	 */
	private int trigger = TRIGGER_AUTO;

	/**
	 * Frame rate the animation should be played at
	 */
	private int frameRate = FRAME_RATE_15;

	/**
	 * If and how the animation should be repeated
	 */
	private int repeatType = REPEAT_ONCE;

	private boolean endAnimationImmediate = false;

	/**
	 * Original frame data
	 */
	private transient byte[][] rawFrames = null;

	/**
	 * Frame data modified with the text to display
	 */
	private transient byte[][] frames = null;

	/**
	 * Text to be dispalyed while animation is on
	 */
	private transient String text = null;

	/**
	 * Position of the text on the frames
	 * (<code>StreamItem.TEXT_POS_BOTTOM/TEXT_CENTER/TEXT_POS_TOP</code>)
	 */
	private transient int textPos = StreamItem.TEXT_POS_BOTTOM;

	private AnimationTrigger animationTrigger;

	/**
	 * Creates AnimationStack without text.
	 * 
	 * @param repeatType
	 *            If and how the animation should be repeated.
	 * @param endAnimationImmediate
	 *            Defines if the animation should be stopped immediate after the
	 *            trigger expires, e.g. aborting the animation before it finishes
	 * @param frameRate
	 *            Frame rate the animation should be played at
	 * @param trigger
	 *            Trigger when the animation should be played
	 * @param frames
	 *            Frames of the animation. In the stream deck compatible format,
	 *            see
	 *            {@link IconHelper#convertImage(java.awt.image.BufferedImage)}
	 */
	public AnimationStack(int repeatType, boolean endAnimationImmediate, int frameRate, int trigger, byte[][] frames) {
		this(repeatType, endAnimationImmediate, frameRate, trigger, frames, null, StreamItem.TEXT_POS_BOTTOM);
	}

	/**
	 * Creates AnimationStack with text.
	 * 
	 * @param repeatType
	 *            If and how the animation should be repeated.
	 * @param frameRate
	 *            Frame rate the animation should be played at
	 * @param trigger
	 *            Trigger when the animation should be played
	 * @param frames
	 *            Frames of the animation. In the stream deck compatible format,
	 *            see
	 *            {@link IconHelper#convertImage(java.awt.image.BufferedImage)}
	 * @param text
	 *            Text to be displayed while the animation is running
	 * @param textPos
	 *            Position of the text on the frames
	 *            ({@link StreamItem#TEXT_POS_TOP},
	 *            {@link StreamItem#TEXT_POS_CENTER},
	 *            {@link StreamItem#TEXT_POS_BOTTOM})
	 */
	public AnimationStack(int repeatType, boolean endAnimationImmediate, int frameRate, int trigger, byte[][] frames,
			String text, int textPos) {
		this.repeatType = repeatType;
		this.frameRate = frameRate;
		this.trigger = trigger;
		this.text = text;
		this.textPos = textPos;
		this.endAnimationImmediate = endAnimationImmediate;
		this.rawFrames = Objects.requireNonNull(frames, "Frames for animation cannot be null.");
		this.frames = new byte[this.rawFrames.length][];
		if (this.text != null) {
			for (int i = 0; i < frames.length; i++) {
				this.frames[i] = IconHelper.addText(this.rawFrames[i], this.text, this.textPos);
			}
		} else {
			System.arraycopy(rawFrames, 0, this.frames, 0, this.rawFrames.length);
		}
	}

	/**
	 * Returns if the animation should be played as soon as the item is
	 * displayed on the stream deck
	 * 
	 * @return <code>true</code> if animation should start immediate as the item
	 *         is displayed on the stream deck
	 */
	public boolean autoPlay() {
		return this.trigger == TRIGGER_AUTO;
	}

	public boolean endAnimationImmediate() {
		return endAnimationImmediate;
	}

	/**
	 * Returns the at the position given by the index
	 * 
	 * @param frameNo
	 * @return
	 */
	public byte[] getFrame(int frameNo) {
		return this.frames[frameNo];
	}

	/**
	 * Returns how many frames this animation has.
	 * 
	 * @return Amount of frames
	 */
	public int getFrameCount() {
		return this.frames.length;
	}

	/**
	 * Reutrns the frame rate at which the animation should be played
	 * 
	 * @return
	 */
	public int getFrameRate() {
		return this.frameRate;
	}

	/**
	 * Returns the repeat type of the animation
	 * 
	 * @return int value of the repeat type
	 */
	public int getRepeatType() {
		return this.repeatType;
	}

	/**
	 * Returns the trigger for the animation
	 * 
	 * @return int value of the trigger for the animation
	 */
	public int getTrigger() {
		return this.trigger;
	}

	/**
	 * Returns if the animation should be triggered.
	 * 
	 * @param keyEventType
	 *            Key event sent by the stream deck
	 * @return <code>true</code> if the animation should be triggered,
	 *         <code>false</code> if not
	 */
	public boolean isTriggered(Type keyEventType) {
		if (this.animationTrigger != null)
			return this.animationTrigger.isTriggered(keyEventType);
		return this.trigger == TRIGGER_PRESSED && keyEventType == Type.PRESSED
				|| this.trigger == TRIGGER_CLICKED && keyEventType == Type.RELEASED_CLICKED
				|| this.trigger == TRIGGER_AUTO;
	}

	/**
	 * Returns if the animation should be looped
	 * 
	 * @return <code>true</code> if the animation should be looped indefinatly,
	 *         <code>false</code> if not
	 */
	public boolean loop() {
		return this.repeatType == REPEAT_LOOPING;
	}

	/**
	 * Returns if the animation should be reversed ass soon as the last frame in
	 * any direction is met
	 * 
	 * @return <code>true</code> if the animation should ping pong between the
	 *         last and the first frame, <code>false</code> if not
	 */
	public boolean pingPong() {
		return this.repeatType == REPEAT_PING_PONG;
	}

	/**
	 * Returns if the animation should be displayed once
	 * 
	 * @return <code>true</code> if the animation should only be displayed once,
	 *         <code>false</code> if not
	 */
	public boolean playOnce() {
		return this.repeatType == REPEAT_ONCE;
	}
	
	public void setAnimationTrigger(AnimationTrigger animationTrigger) {
		this.animationTrigger = animationTrigger;
	}

	/**
	 * Sets if the animation should be stopped immediate after the trigger is
	 * not applicable anymore.
	 * 
	 * @param endAnimationImmediate
	 */
	public void setEndAnimationImmediate(boolean endAnimationImmediate) {
		this.endAnimationImmediate = endAnimationImmediate;
	}

	/**
	 * Sets the frames for the animation
	 * 
	 * @param frames
	 *            frames of the animation
	 */
	public void setFrames(byte[][] frames) {
		this.rawFrames = frames;
		byte[][] nframes = new byte[this.rawFrames.length][];
		if (this.text != null) {
			for (int i = 0; i < nframes.length; i++) {
				nframes[i] = IconHelper.addText(this.rawFrames[i], this.text, this.textPos);
			}
		} else {
			System.arraycopy(rawFrames, 0, nframes, 0, this.rawFrames.length);
		}
		this.frames = nframes;
	}

	/**
	 * Sets the text to be displayed on the animation
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		// create new local frames
		byte[][] nframes = new byte[this.rawFrames.length][];
		if (this.text != null) {
			for (int i = 0; i < nframes.length; i++) {
				nframes[i] = IconHelper.addText(this.rawFrames[i], this.text, this.textPos);
			}
		} else {
			System.arraycopy(rawFrames, 0, nframes, 0, this.rawFrames.length);
		}
		// swap local frames with the stacks frames
		this.frames = nframes;
	}

	/**
	 * Sets the text position of the displayed text
	 * 
	 * @param textPos
	 */
	public void setTextPos(int textPos) {
		this.textPos = textPos;
		if (this.text != null) {
			// Create frames with new text pos
			byte[][] nframes = new byte[this.rawFrames.length][];
			for (int i = 0; i < nframes.length; i++) {
				nframes[i] = IconHelper.addText(this.rawFrames[i], this.text, this.textPos);
			}
			// swap local frames with the stacks frames
			this.frames = nframes;
		}
	}
}