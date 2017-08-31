package de.rcblum.stream.deck;

import java.awt.image.BufferedImage;

public interface StreamItem  {
	
	public int getKeyIndex();
	
	public default StreamItem[] getChildren() {return null;}
	
	public default boolean isLeaf() {return getChildren() == null;}
	
	public BufferedImage getIcon();
	
	public void onClick();

	public void onPress();
	
	public void onRelease();
	
}
