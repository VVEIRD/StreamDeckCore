package de.rcblum.stream.deck;

import java.awt.image.BufferedImage;

/**
 * Interface to bind actions to a key of a stream deck
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
 * @version 0.1
 *
 */
public interface StreamItem  {
	
	public int getKeyIndex();
	
	/**
	 * Returns an array with the children of this item
	 * @return Children of this item, or null, if not a folder
	 */
	public default StreamItem[] getChildren() {return null;}
	
	/**
	 * Returns the number of children contained by this item
	 * @return number of children, 0 if no children or not a folder
	 */
	public default int getChildrenCount() {return 0;}
	
	/**
	 * Returns whether this item has children --> is a folder, or if it is a simple key.
	 * @return
	 */
	public default boolean isLeaf() {return getChildren() == null;}
	
	/**
	 * Returns the icon to be displayed on the stream deck. 
	 * @return
	 */
	public BufferedImage getIcon();
	
	/**
	 * Will be called if the linked button is pressed, then released
	 */
	public void onClick();

	/**
	 * Will be called if the linked button on the stream deck is pressed down
	 */
	public void onPress();
	
	/**
	 * Will be called if the linked button on the stream deck is released
	 */
	public void onRelease();
	
	/**
	 * This method will be called when the item is brought on one of the buttons
	 * of the stream deck
	 */
	public void onDisplay();
	
	/**
	 * This method will be called if the item is removed from the stream deck
	 */
	public void offDisplay();
	
}
