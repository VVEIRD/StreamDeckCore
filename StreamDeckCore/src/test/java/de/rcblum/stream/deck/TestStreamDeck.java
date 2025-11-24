package de.rcblum.stream.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;

public class TestStreamDeck {
	private static Logger log = LogManager.getLogger(TestStreamDeck.class);

	static IStreamDeck sd = null;

	@BeforeAll
	public static void init() {
		System.out.println("------------------------- INIT --------------------------");
		StreamDeckDevices.enableSoftwareStreamDeck();
		sd = StreamDeckDevices.getStreamDeck();
	}

	@Test
	public void testButton() {
		System.out.println();
		System.out.println("------------------- START Test Button -------------------");
		System.out.println();
		ButtonListener skl = new ButtonListener();
		int button = 5;
		sd.addKeyListener(skl);
		System.out.println("  - EVENTS ---------------");
		// Test 1: Press button 5
		sd.pressButton(button);
		KeyEvent ke = skl.getNext(1_000);
		assertNotNull(ke);
		assertEquals(button, ke.getKeyId());
		assertEquals(KeyEvent.Type.PRESSED, ke.getType());
		System.out.println("  Key Event: " + ke);
		System.out.println("  Button:    " + ke.getKeyId());
		System.out.println("  Type:      " + ke.getType());
		System.out.println("  ------------------------");
		// Test 2: Release Button 5
		sd.releaseButton(button);
		ke = skl.getNext(1_000);
		assertNotNull(ke);
		assertEquals(button, ke.getKeyId());
		assertEquals(KeyEvent.Type.RELEASED_CLICKED, ke.getType());
		System.out.println("  Key Event: " + ke);
		System.out.println("  Button:    " + ke.getKeyId());
		System.out.println("  Type:      " + ke.getType());
		System.out.println("  ------------------------");
		// Test 3: Click button 3
		button = 3;
		sd.pushButton(button);
		ke = skl.getNext(1_000);
		assertNotNull(ke);
		assertEquals(button, ke.getKeyId());
		assertEquals(KeyEvent.Type.RELEASED_CLICKED, ke.getType());
		System.out.println("  Key Event: " + ke);
		System.out.println("  Button:    " + ke.getKeyId());
		System.out.println("  Type:      " + ke.getType());
		System.out.println("  ------------------------");
		System.out.println();
		
		
		System.out.println("-------------------- END Test Button --------------------");
	}

	@AfterAll
	public static void deinit() {
		System.out.println("------------------------ DEINIT -------------------------");
		sd.stop();
		sd = null;
	}

	class ButtonListener implements StreamKeyListener {
		ConcurrentLinkedQueue<KeyEvent> event = new ConcurrentLinkedQueue<>();

		@Override
		public void onKeyEvent(KeyEvent event) {
			this.event.add(event);
		}

		public KeyEvent getNext(int timeout) {
			long start = System.currentTimeMillis();
			while (event.isEmpty() && System.currentTimeMillis() - start < timeout)
				;
			if (System.currentTimeMillis() - start > timeout)
				System.out.println("  Timeout for polling an KeyEvent reached");
			return event.poll();
		}
	}
}
