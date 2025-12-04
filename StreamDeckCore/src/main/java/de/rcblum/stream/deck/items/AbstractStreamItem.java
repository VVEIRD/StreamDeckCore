package de.rcblum.stream.deck.items;

import java.util.LinkedList;
import java.util.List;

import de.rcblum.stream.deck.animation.AnimationStack;
import de.rcblum.stream.deck.device.StreamDeck;
import de.rcblum.stream.deck.device.StreamDeckConstants;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.items.listeners.IconUpdateListener;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;
import de.rcblum.stream.deck.util.SDImage;

/**
 * Abstract version of StreamItem, implements al relevant functions for icons
 * and animations.
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
 * @version 1.0.0
 *
 */
public abstract class AbstractStreamItem implements StreamItem {

	protected String text = null;

	protected int textPos = StreamItem.TEXT_POS_BOTTOM;

	/**
	 * Parent of the item
	 */
	protected StreamItem parent = null;

	/**
	 * Raw image of the folder
	 */
	protected SDImage rawImg = null;

	/**
	 * Image with text if present.
	 */
	protected SDImage img = null;

	/**
	 * Animation for the key, if present
	 */
	protected AnimationStack animation = null;

	/**
	 * Listeners for updates to the icons
	 */
	List<IconUpdateListener> listeners = null;
	
	/**
	 * Button count of the connected StreamDeck
	 */	
	protected int buttonCount = StreamDeckConstants.BUTTON_COUNT;

	/**
	 * Row count of the connected StreamDeck
	 */	
	protected int rowCount = StreamDeckConstants.ROW_COUNT;	

	public AbstractStreamItem(SDImage img) {
		this(img, null, StreamDeckConstants.BUTTON_COUNT, StreamDeckConstants.ROW_COUNT);
	}

	public AbstractStreamItem(IconPackage pkg) {
		this(pkg.icon, pkg.animation, null, StreamItem.TEXT_POS_BOTTOM, StreamDeckConstants.BUTTON_COUNT, StreamDeckConstants.ROW_COUNT);
	}

	public AbstractStreamItem(SDImage img, AnimationStack animation) {
		this(img, animation, null, StreamItem.TEXT_POS_BOTTOM, StreamDeckConstants.BUTTON_COUNT, StreamDeckConstants.ROW_COUNT);
	}

	public AbstractStreamItem(SDImage img, AnimationStack animation, String text) {
		this(img, animation, text, StreamItem.TEXT_POS_BOTTOM, StreamDeckConstants.BUTTON_COUNT, StreamDeckConstants.ROW_COUNT);
	}
	
	public AbstractStreamItem(SDImage img, int buttonCount, int rowCount) {
		this(img, null, buttonCount, rowCount);
	}

	public AbstractStreamItem(IconPackage pkg, int buttonCount, int rowCount) {
		this(pkg.icon, pkg.animation, null, StreamItem.TEXT_POS_BOTTOM, buttonCount, rowCount);
	}

	public AbstractStreamItem(SDImage img, AnimationStack animation, int buttonCount, int rowCount) {
		this(img, animation, null, StreamItem.TEXT_POS_BOTTOM, buttonCount, rowCount);
	}

	public AbstractStreamItem(SDImage rawImg, AnimationStack animation, String text, int textPos, int buttonCount, int rowCount) {
		super();
		this.text = text;
		this.textPos = textPos;
		this.rawImg = rawImg;
		this.animation = animation;
		this.listeners = new LinkedList<>();
		this.buttonCount = buttonCount;
		this.rowCount = rowCount;
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		if (this.text != null && this.animation != null) {
			this.animation.setText(this.text, this.textPos);
		}
	}

	@Override
	public SDImage getIcon() {
		return this.img;
	}

	@Override
	public void setIconPackage(IconPackage iconPackage) {
		setIcon(iconPackage.icon);
		this.animation = iconPackage.animation;
		if (this.text != null && this.animation != null) {
			this.animation.setTextPos(this.textPos);
			this.animation.setText(this.text);
		}
		this.rawImg = iconPackage.icon;
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		this.fireIconUpdate(true);
	}
	
	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public void setAnimation(AnimationStack animation) {
		this.animation = animation;
		if (this.text != null && this.animation != null) {
			this.animation.setTextPos(this.textPos);
			this.animation.setText(this.text);
		}
		this.fireIconUpdate(true);
	}

	@Override
	public void setIcon(SDImage icon) {
		this.rawImg = icon;
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		this.fireIconUpdate(false);
	}
	
	@Override
	public int getButtonCount() {
		return buttonCount;
	}

	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	
	public int getColumnCount() {
		return this.buttonCount/this.rowCount;
	}

	@Override
	public StreamItem getParent() {
		return this.parent;
	}

	public void setParent(StreamItem parent) {
		this.parent = parent;
	}

	@Override
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.setText(text, this.textPos);
	}

	public void setText(String text, int textPos) {
		boolean change = this.text != text || this.textPos != textPos || text != null && !text.equals(this.text);
		this.text = text;
		this.textPos = textPos;
		if (change && this.text != null) {
			if (this.animation != null) {
				this.animation.setText(this.text, this.textPos);
			}
			this.img = IconHelper.addText(this.rawImg, this.text, this.textPos);
			this.fireIconUpdate(false);
		}
		else if (change) {
			if (this.animation != null) {
				this.animation.setText(null, this.textPos);
			}
			this.img = this.rawImg;
			this.fireIconUpdate(false);
		}
	}

	public void setTextPosition(int textPos) {
		this.setText(this.text, textPos);
	}

	@Override
	public boolean hasAnimation() {
		return this.animation != null;
	}

	@Override
	public AnimationStack getAnimation() {
		return this.animation;
	}

	public void addIconUpdateListener(IconUpdateListener listener) {
		this.listeners.add(listener);
	}

	public void removeIconUpdateListener(IconUpdateListener listener) {
		this.listeners.remove(listener);
	}

	protected void fireIconUpdate(boolean animationChanged) {
		if (this.listeners != null) {
			for (int i = 0; i < this.listeners.size(); i++) {
				this.listeners.get(i).onIconUpdate(this, animationChanged);
			}
		}
	}

	@Override
	public boolean hasDialListeners() {
		return false;
	}

	@Override
	public StreamKeyListener[] getDialListeners() {
		return null;
	}

	@Override
	public boolean hasTouchScreenListeners() {
		return false;
	}

	@Override
	public StreamKeyListener[] getTouchScreenListeners() {
		return null;
	}
	
	@Override
	public SDImage getTouchScreenImage() {
		return null;
	}
	
	@Override
	public boolean hasTouchScreenImage() {
		return false;
	}
}
