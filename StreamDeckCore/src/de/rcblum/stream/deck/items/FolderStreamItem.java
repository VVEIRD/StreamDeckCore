package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.util.IconHelper;

/**
 * Simple Stream Item that can contain other stream items. 
 * @author rcBlum
 *
 */
public class FolderStreamItem implements StreamItem {

	private StreamItem parent = null;

	private StreamItem[] children = null;

	private byte[] img = null;

	public FolderStreamItem(StreamItem parent, StreamItem[] children) {
		super();
		this.parent = parent;
		this.children = children;
		this.img = IconHelper.getImage("temp://FOLDER");
		for (int i = 0; i < children.length; i++) {
			if (this.children[i] != null) {
				this.children[i].setParent(this);
			}
		}
	}

	public void setParent(StreamItem parent) {
		this.parent = parent;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
	}

	@Override
	public byte[] getIcon() {
		return this.img;
	}

	@Override
	public StreamItem getParent() {
		return this.parent;
	}

	@Override
	public StreamItem[] getChildren() {
		return this.children;
	}

}
