package de.rcblum.stream.deck.device;

import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

/**
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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
 *
 */
public interface IStreamDeck {

	/**
	 * Adds a {@link StreamKeyListener} to the given index
	 * @param keyId	Index of the key, 0..14
	 * @param item StreamItem to be bound to the index
	 * @throws IndexOutOfBoundsException when keyId is &#60; 0 or &#62; 14.
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
	 * @param brightness	Brightness in percentile
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

	/**
	 * Returns the amount of keys on the StreamDeck.
	 * @return Numbers of keys on the deck (Default 15).
	 */
	public default int getKeySize() { return 15;};

}