package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.items.listeners.IconUpdateListener;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;

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

	public static int TEXT_POS_TOP = IconHelper.TEXT_TOP;

	public static int TEXT_POS_CENTER = IconHelper.TEXT_CENTER;

	public static int TEXT_POS_BOTTOM = IconHelper.TEXT_BOTTOM;

	/**
	 * Returns an array with the children of this item. If the item has a
	 * parent, the parent must be the 5th item ({@link #getChildren()}[4] ) in
	 * the array.
	 * 
	 * @return Children of this item, or null, if not a folder
	 */
	public default StreamItem[] getChildren() {
		return null;
	}

	public default StreamItem getChild(int i) {
		return null;
	}

	/**
	 * Returns the Index of the child
	 * 
	 * @param item
	 *            Child to be identified
	 * @return Index of the child or -1 if its not a child.
	 */
	public default int getChildId(StreamItem item) {
		return -1;
	}

	/**
	 * Returns whether this item has children --> is a folder, or if it is a
	 * simple key.
	 * 
	 * @return
	 */
	public default boolean isLeaf() {
		return getChildren() == null;
	}

	public StreamItem getParent();

	public void setParent(StreamItem parent);

	/**
	 * Returns the icon to be displayed on the stream deck.
	 * 
	 * @return
	 */
	public byte[] getIcon();

	public boolean hasAnimation();

	public AnimationStack getAnimation();

	public String getText();

	public void setText(String text);

	public void setTextPosition(int textPos);

	public void setIconPackage(IconPackage iconPackage);

	public void setIcon(byte[] icon);

	public void setAnimation(AnimationStack animation);

	public void addIconUpdateListener(IconUpdateListener listener);

	public void removeIconUpdateListener(IconUpdateListener listener);

}
