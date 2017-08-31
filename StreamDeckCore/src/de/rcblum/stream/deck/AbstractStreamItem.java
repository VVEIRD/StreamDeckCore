package de.rcblum.stream.deck;

import java.awt.image.BufferedImage;

public abstract class AbstractStreamItem implements StreamItem {
	
	protected int id = -1;
	
	protected BufferedImage img = null;

	public AbstractStreamItem(int keyIndex, BufferedImage img) {
		super();
		this.id = keyIndex;
		this.img = img;
	}

	@Override
	public int getKeyIndex() {
		return this.id;
	}

	@Override
	public BufferedImage getIcon() {
		return this.img;
	}
}
