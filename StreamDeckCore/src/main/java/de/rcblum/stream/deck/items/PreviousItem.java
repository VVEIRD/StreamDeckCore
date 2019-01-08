package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.items.animation.AnimationStack;

public class PreviousItem extends FolderItem{

	StreamItem next = null;
	
	public PreviousItem(StreamItem next) {
		super("Previous", null, new StreamItem[15]);
		this.next = next;
		this.setTextPosition(TEXT_POS_CENTER);
		this.setText("Previous");
	}
	
	@Override
	public StreamItem getChild(int i) {
		return next.getChild(i);
	}
	
	@Override
	public AnimationStack getAnimation() {
		return next.getAnimation();
	}
	
	@Override
	public int getChildId(StreamItem item) {
		return next.getChildId(item);
	}
	
	@Override
	public StreamItem[] getChildren() {
		return next.getChildren();
	}
	
	@Override
	public StreamItem getParent() {
		return next.getParent();
	}
	
	@Override
	public void setParent(StreamItem parent) {
		next.setParent(parent);
	}
}
