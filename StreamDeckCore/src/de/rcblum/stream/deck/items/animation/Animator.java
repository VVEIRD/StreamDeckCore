package de.rcblum.stream.deck.items.animation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.rcblum.stream.deck.StreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;

public class Animator implements StreamKeyListener, Runnable {


	private StreamDeck streamDeck = null;
	
	private int keyIndex = -1;
	
	private AnimationStack animation = null;
	
	private int framePos = 0;
	
	private int frameAdvance = 1;
	
	private ScheduledExecutorService scheduler = null;
	
	private boolean stopAfterAnimation = false;
	
	public Animator(StreamDeck streamDeck, int keyIndex, AnimationStack animation) {
		System.out.println("New animator: " + keyIndex);
		this.streamDeck = streamDeck;
		this.keyIndex = keyIndex;
		this.animation = animation;
		System.out.println("Autoplay: " + this.animation.autoPlay());
		if (this.animation.autoPlay()) 
			this.start();
	}
	
	public void onKeyEvent(KeyEvent event) {
		boolean triggered = this.animation.isTriggered(event.getType());
		if (triggered && event.getKeyId() == keyIndex && this.scheduler == null) {
			this.start();
		}
		else if (!triggered && event.getKeyId() == keyIndex && this.scheduler != null) {
			this.stop(false);
		}
	}
	
	public void start() {
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.scheduler.scheduleAtFixedRate(
			this,
			1_000_000/this.animation.getFrameRate(),
			1_000_000/this.animation.getFrameRate(),
			TimeUnit.MICROSECONDS
		);
	}
	
	public void stop(boolean immediate) {
		if (this.scheduler != null) {
			if (immediate) {
				this.scheduler.shutdown();
				this.scheduler = null;
				this.framePos = 0;
				this.frameAdvance = 1;
				this.stopAfterAnimation = false;
			}
			else {
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
}