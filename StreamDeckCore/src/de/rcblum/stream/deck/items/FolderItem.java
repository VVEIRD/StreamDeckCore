package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.util.IconHelper;

/**
 * Simple Stream Item that can contain other stream items.
 * 
 * @author rcBlum
 *
 */
public class FolderItem extends AbstractStreamItem {

	/**
	 * Items contained by this folder. Index 4 must be empty if the parent
	 * present. Index 4 will be ignored if a parent is present.
	 */
	private StreamItem[] children = null;

	public FolderItem(String folderName, StreamItem parent, StreamItem[] children) {
		this(folderName, parent, children, TEXT_POS_BOTTOM);
	}

	public FolderItem(String folderName, StreamItem parent, StreamItem[] children, int textPosition) {
		super(IconHelper.getImage("temp://FOLDER"), null, folderName, textPosition);
		this.parent = parent;
		this.children = children;
		this.rawImg = IconHelper.getImage("temp://FOLDER");
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, IconHelper.TEXT_BOTTOM)
				: this.rawImg;
		for (int i = 0; i < children.length; i++) {
			if (this.children[i] != null) {
				this.children[i].setParent(this);
			}
		}
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
	}

	public void setFolderName(String folderName) {
		this.text = folderName;
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, IconHelper.TEXT_BOTTOM)
				: this.rawImg;
	}

	@Override
	public byte[] getIcon() {
		return this.img;
	}

	@Override
	public StreamItem[] getChildren() {
		return this.children;
	}

	@Override
	public StreamItem getChild(int i) {
		return i >= 0 && i < 15 ? this.children[i] : null;
	}

	@Override
	public int getChildId(StreamItem item) {
		if (item == null)
			return -1;
		for (int i = 0; i < children.length; i++) {
			if (item == this.children[i])
				return i;
		}
		return -1;
	}

}
