package de.rcblum.stream.deck.items;

import java.util.LinkedList;
import java.util.List;

import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.items.listeners.IconUpdateListener;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;

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
 * @version 0.1
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
	protected byte[] rawImg = null;

	/**
	 * Image with text if present.
	 */
	protected byte[] img = null;

	/**
	 * Animation for the key, if present
	 */
	protected AnimationStack animation = null;

	/**
	 * Listeners for updates to the icons
	 */
	List<IconUpdateListener> listeners = null;

	public AbstractStreamItem(byte[] img) {
		this(img, null);
	}

	public AbstractStreamItem(IconPackage pkg) {
		this(pkg.icon, pkg.animation, null);
	}

	public AbstractStreamItem(byte[] img, AnimationStack animation) {
		this(img, animation, null);
	}

	public AbstractStreamItem(byte[] img, AnimationStack animation, String text) {
		this(img, animation, text, StreamItem.TEXT_POS_BOTTOM);
	}

	public AbstractStreamItem(byte[] rawImg, AnimationStack animation, String text, int textPos) {
		super();
		this.text = text;
		this.textPos = textPos;
		this.rawImg = rawImg;
		this.animation = animation;
		this.listeners = new LinkedList<>();
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		if (this.text != null && this.animation != null) {
			this.animation.setTextPos(this.textPos);
			this.animation.setText(this.text);
		}
	}

	@Override
	public byte[] getIcon() {
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
		this.fireIconUpdate();
	}

	@Override
	public void setAnimation(AnimationStack animation) {
		this.animation = animation;
		if (this.text != null && this.animation != null) {
			this.animation.setTextPos(this.textPos);
			this.animation.setText(this.text);
		}
		this.fireIconUpdate();
	}

	@Override
	public void setIcon(byte[] icon) {
		this.rawImg = icon;
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		this.fireIconUpdate();
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
		boolean change = this.text != text || text != null && !text.equals(this.text);
		this.text = text;
		if (change) {
			if (this.animation != null) {
				this.animation.setTextPos(this.textPos);
				this.animation.setText(this.text);
			}
			this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
			this.fireIconUpdate();
		}
	}

	public void setTextPosition(int textPos) {
		this.textPos = textPos;
		if (this.text != null) {
			this.img = IconHelper.addText(this.rawImg, this.text, this.textPos);
			if (this.animation != null) {
				this.animation.setTextPos(this.textPos);
			}
			this.fireIconUpdate();
		}
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

	protected void fireIconUpdate() {
		for (int i = 0; i < this.listeners.size(); i++) {
			if (this.listeners != null)
				this.listeners.get(i).onIconUpdate(this);
		}
	}
}
