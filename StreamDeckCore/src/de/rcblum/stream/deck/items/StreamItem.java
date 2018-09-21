package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.items.listeners.IconUpdateListener;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;
import de.rcblum.stream.deck.util.SDImage;

/**
 * Interface to bind actions to a key of a stream deck. Can also be used with
 * the {@link StreamDeckController}.
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
public interface StreamItem extends StreamKeyListener {

	/**
	 * Value to indicate that the text for this item should be displayed at the top.
	 */
	public static int TEXT_POS_TOP = IconHelper.TEXT_TOP;

	/**
	 * Value to indicate that the text for this item should be displayed at the center.
	 */
	public static int TEXT_POS_CENTER = IconHelper.TEXT_CENTER;

	/**
	 * Value to indicate that the text for this item should be displayed at the bottom.
	 */
	public static int TEXT_POS_BOTTOM = IconHelper.TEXT_BOTTOM;

	/**
	 * Returns an array with the children of this item. If the item has a parent,
	 * the parent must be the 5th item ({@link #getChildren()}[4] ) in the array.
	 * 
	 * @return Children of this item, or null, if not a folder
	 */
	public default StreamItem[] getChildren() {
		return null;
	}
	
	public int getChildCount();

	public default StreamItem getChild(int i) {
		return null;
	}

	/**
	 * Returns the Index of the child
	 * 
	 * @param item Child to be identified
	 * @return Index of the child or -1 if its not a child.
	 */
	public default int getChildId(StreamItem item) {
		return -1;
	}

	/**
	 * Returns whether this item has children --> is a folder, or if it is a simple
	 * key.
	 * 
	 * @return <code>true</code> if the Streamitem has children, <code>false</code>
	 *         if not
	 */
	public default boolean isLeaf() {
		return getChildren() == null;
	}

	/**
	 * Returns the parent of the item.
	 * 
	 * @return StreamItem that is the parent of this item, <code>null</code> if the
	 *         StreamItem has nor parent.
	 */
	public StreamItem getParent();

	public void setParent(StreamItem parent);

	/**
	 * Returns the icon to be displayed on the stream deck.
	 * 
	 * @return Returns the icon object of the Item
	 */
	public SDImage getIcon();

	/**
	 * Returns if the item has an animation that can be displayed
	 * 
	 * @return <code>true</code> if an animation exists, <code>false</code> if the
	 *         item has no animation.
	 */
	public boolean hasAnimation();

	/**
	 * Returns the animation of the item.
	 * 
	 * @return Animation in form of the {@link AnimationStack}, <code>null</code> if
	 *         the item has no no animation
	 */
	public AnimationStack getAnimation();

	/**
	 * Retrns the text on the item.
	 * 
	 * @return Text or <code>null</code>, if there is no text
	 */
	public String getText();

	/**
	 * Sets the text to be displayed of the item.
	 * 
	 * @param text Text to be displayed
	 */
	public void setText(String text);

	/**
	 * Position of the displayed text.
	 * 
	 * @param textPos Position of the text (Top - {@link #TEXT_POS_TOP}, center-
	 *                {@link #TEXT_POS_CENTER}, bottom, {@link #TEXT_POS_BOTTOM})
	 */
	public void setTextPosition(int textPos);

	/**
	 * Apply the icon and animation of the {@link IconPackage} to the item
	 * 
	 * @param iconPackage IconPackage to be applied
	 */
	public void setIconPackage(IconPackage iconPackage);

	/**
	 * Sets the icon for the item.
	 * 
	 * @param icon New icon for the item.
	 */
	public void setIcon(SDImage icon);

	/**
	 * Sets the animation of the item
	 * 
	 * @param animation {@link AnimationStack} with the animation
	 */
	public void setAnimation(AnimationStack animation);

	/**
	 * Adds an {@link IconUpdateListener} to the item.
	 * 
	 * @param listener Listener that listens for changes on the icon
	 */
	public void addIconUpdateListener(IconUpdateListener listener);

	/**
	 * Removes the given listener. If the listener has not been added before it will
	 * do nothing.
	 * 
	 * @param listener Listener to be removed.
	 */
	public void removeIconUpdateListener(IconUpdateListener listener);

}
