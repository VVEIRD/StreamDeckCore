package de.rcblum.stream.deck.device.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.StreamDeck;

/**
 * Dispatches all commands asynchronously queued up in {@link StreamDeck#sendPool} to the ESD.
 * Send rate is limited to 500 commands per second.
 * If the execution of one command is completed in less tha on ms the thread is put to sleep for 1 ms.
 * As long as one loop takes up less then 2 ms the rest of the time is actively wated
 * 
 * @author Roland von Werden
 *
 */
public class DeckWorker implements Runnable {
	
	private static final Logger LOGGER = LogManager.getLogger(DeckWorker.class);
	
	StreamDeck streamDeck;
	
	public DeckWorker(StreamDeck streamDeck) {
		this.streamDeck = streamDeck;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void run() {
		long actions = 0;
		long time = System.currentTimeMillis();
		long t = 0;
		while (this.streamDeck.isRunning() || !this.streamDeck.isRunning() && !this.streamDeck.isSendPoolEmpty()) {
			// Cleanup clogged send pool
			if(this.streamDeck.getSendPoolSize() > 100) {
				Runnable[] payloads = new Runnable[this.streamDeck.getKeySize()];
				Runnable resetTask = null;
				Runnable brightnessTask = null;
				Runnable task = null;
				while ((task = this.streamDeck.pollSendPool()) != null) {
					if (task instanceof IconUpdater) {
						IconUpdater iu = (IconUpdater)task;
						payloads[iu.keyIndex] = iu;
					}
					else if(task instanceof Resetter)
						resetTask = task;
					else if (task instanceof BrightnessUpdater) 
						brightnessTask = task;
				}
				if(brightnessTask != null)
					this.streamDeck.addToSendPool(brightnessTask);
				if(resetTask != null)
					this.streamDeck.addToSendPool(resetTask);
				for (int i = 0; i < payloads.length; i++) {
					if(payloads[i] != null)
						this.streamDeck.addToSendPool(payloads[i]);
				}
			}
			t = System.nanoTime();
			Runnable task = this.streamDeck.pollSendPool();
			if (task != null) {
				try {
					task.run();
				} catch (Exception e) {
					LOGGER.error("Error sending the following command-class th the esd: " + task.getClass() );
					LOGGER.error(e);
				}
			}
			if (System.nanoTime()-t < 1_000)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					LOGGER.error("DeckWorker interrupted", e);
					Thread.currentThread().interrupt();
				}
			if(LOGGER.isDebugEnabled()) {
				actions++;
				if(System.currentTimeMillis() - time > 30_000) {
					LOGGER.debug("Commands send per one second: " + (actions/30));
					LOGGER.debug("Commands send per 30 seconds: " + actions);
					time = System.currentTimeMillis();
					actions = 0;
				}
			}
			while(System.nanoTime()-t < 2_000) {
				Math.subtractExact(2, 1);
			}
		}
	}
}