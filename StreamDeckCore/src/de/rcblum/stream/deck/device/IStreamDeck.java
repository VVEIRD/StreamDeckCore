package de.rcblum.stream.deck.device;

import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

public interface IStreamDeck {

	/**
	 * Adds a {@link StreamKeyListener} to the given index
	 * @param keyId	Index of the key, 0..14
	 * @param item StreamItem to be bound to the index
	 * @throws IndexOutOfBoundsException when keyId is < 0 or > 14.
	 */
	void addKey(int keyId, StreamItem item);

	/**
	 * Adds an StreamKeyListener to the ESD. WHenever a Event is generated, the Listener will be informed.
	 * @param listener	Listener to be added
	 * @return	<code>true</code> if listener was added, <code>false</code> if listener is already registered.
	 */
	boolean addKeyListener(StreamKeyListener listener);

	/**
	 * Removes an StreamKeyListener from the ESD.
	 * @param listener	Listener to be removed
	 * @return	<code>true</code> if listener was removed, <code>false</code> if listener is not registered.
	 */
	boolean removeKeyListener(StreamKeyListener listener);

	/**
	 * Creates a Job to send the give icon to the ESD to be displayed on the given keyxIndex
	 * @param keyIndex	Index of ESD (0..14)
	 * @param imgData	Image in BGR format to be displayed
	 */
	void drawImage(int keyIndex, SDImage imgData);

	/**
	 * Returns the Hid Devices representation the stream deck.
	 * 
	 * @return HidDevice representation the stream deck.
	 */
	HidDevice getHidDevice();

	/**
	 * Removes a registered Key. Queues update to the stream deck
	 * 
	 * @param keyId
	 *            id of the key to be removed
	 */
	void removeKey(int keyId);

	/**
	 * Queues a task to reset the stream deck.
	 */
	void reset();

	/**
	 * Sets the desired brightness from 0 - 100 % and queues the change.
	 * 
	 * @param brightness
	 */
	void setBrightness(int brightness);

	/**
	 * Tells the background task for the stream deck to stop working.
	 */
	void stop();

	/**
	 * Wait for all tasks to be executed
	 */
	void waitForCompletion();

	void clearButton(int i);
	
	/**
	 * Returns if behind the interface is actual hardware or a software only StreamDeck.
	 * @return <code>true</code> if hardware is sued, <code>false</code> if the implementation is only software.
	 */
	public boolean isHardware();

}