package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.util.IconHelper;

/**
 * Simple Stream Item that can contain other stream items. 
 * @author rcBlum
 *
 */
public class FolderItem implements StreamItem {

	public static int TEXT_POS_TOP = IconHelper.TOP;

	public static int TEXT_POS_CENTER = IconHelper.CENTER;

	public static int TEXT_POS_BOTTOM = IconHelper.BOTTOM;
	
	/**
	 * Name of the folder
	 */
	private String folderName = null; 

	/**
	 * Parent of the item
	 */
	private StreamItem parent = null;

	/**
	 * Items contained by this folder. Index 4 must be empty if the parent
	 * present. Index 4 will be ignored if a parent is present.
	 */
	private StreamItem[] children = null;

	/**
	 * Raw image of the folder
	 */
	private byte[] rawImg = null;
	
	/**
	 * Image with foldername if present.
	 */
	private byte[] img = null;

	public FolderItem(String folderName, StreamItem parent, StreamItem[] children) {
		this(folderName, parent, children, TEXT_POS_BOTTOM);
	}

		public FolderItem(String folderName, StreamItem parent, StreamItem[] children, int textPosition) {
		super();
		this.folderName = folderName;
		this.parent = parent;
		this.children = children;
		this.rawImg = IconHelper.getImage("temp://FOLDER");
		this.img = this.folderName != null ? IconHelper.addText(this.rawImg, this.folderName, IconHelper.BOTTOM) : this.rawImg;
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
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
		this.img = this.folderName != null ? IconHelper.addText(this.rawImg, this.folderName, IconHelper.BOTTOM) : this.rawImg;
	}
	
	public String getFolderName() {
		return folderName;
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
