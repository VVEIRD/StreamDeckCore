package de.rcblum.stream.deck.device.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.StreamDeck;

/**
 * Dispatches all commands asynchronously queued up in {@link StreamDeck#addToSendPool(DeckUpdater)} to the ESD.
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
				DeckUpdater[] payloads = new DeckUpdater[this.streamDeck.getKeySize()];
				DeckUpdater resetTask = null;
				DeckUpdater brightnessTask = null;
				DeckUpdater task = null;
				while ((task = this.streamDeck.pollSendPool()) != null) {
					if (task.drawImageInterface != null) {
						payloads[task.keyIndex] = task;
					}
					else if(task.featureReportInterface != null)
						resetTask = task;
					else if (task.featureReportIntInterface != null) 
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
					LOGGER.debug("Send Pool Backlog: " + this.streamDeck.getSendPoolSize());
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