package de.rcblum.stream.deck.items;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.device.general.StreamDeck;
import de.rcblum.stream.deck.items.animation.AnimationStack;

public class PagedFolderItem extends FolderItem {

	private static Logger logger = LogManager.getLogger(PagedFolderItem.class);


	public PagedFolderItem(String folderName, StreamItem parent, PagedFolderItem previous, StreamItem[] children, int keyCount, int rowCount) {
		super(folderName, parent, new StreamItem[keyCount]);
		this.setButtonCount(keyCount);
		this.setRowCount(rowCount);
		children = children != null ? children : new StreamItem[keyCount];
		int maxItems = keyCount - (rowCount+1);
		// Create Sub-folder for more then 9/10 children (with parent/without parent)
		int countTotal=0;
		for (StreamItem streamItem : children) {
			if (streamItem != null)
				countTotal++;
		}
		// Fill current page
		int childIndex = 0;
		int countSI = 0;
		for(;childIndex<children.length&&countSI<maxItems;childIndex++) {
			if(children[childIndex] != null) {
				this.getChildren()[countSI<this.getColumnCount()-1? countSI : countSI+1] = children[childIndex];
				children[childIndex].setParent(this);
				countSI++;
			}
		}
		if (previous != null) {
			this.getChildren()[(this.rowCount * (this.getColumnCount()-1))+1] = new PageItem(previous, "Previous", this.getButtonCount());
		}
		if (countTotal > maxItems && children.length-childIndex > 0) {
			logger.debug("Create next page for " + this.getText());
			// Create children for next page
			StreamItem[] nextPageChildren = Arrays.copyOfRange(children, childIndex, children.length);
			// Create next page
			PagedFolderItem next = new PagedFolderItem(folderName, parent, this, nextPageChildren, this.getButtonCount(), this.getRowCount());
			next.setTextPosition(TEXT_POS_CENTER);
			next.setText("Next");
			this.getChildren()[this.getRowCount() * (this.getColumnCount()-1)] = new PageItem(next, "Next", this.getButtonCount());
		}
	}
	
	@Override
	public void setParent(StreamItem parent) {
		super.setParent(parent);
		if (this.getChild(this.getRowCount() * (this.getColumnCount()-1)) != null)
			this.getChild(this.getRowCount() * (this.getColumnCount()-1)).setParent(parent);
	}
	
	public static class PageItem extends FolderItem {

		PagedFolderItem page = null;
		
		public PageItem(PagedFolderItem page, String name, int keyCount) {
			super(name, null, new StreamItem[keyCount]);
			this.page = page;
			this.setTextPosition(TEXT_POS_CENTER);
			this.setText(name);
		}
		
		@Override
		public StreamItem getChild(int i) {
			return page.getChild(i);
		}
		
		@Override
		public AnimationStack getAnimation() {
			return page.getAnimation();
		}
		
		@Override
		public int getChildId(StreamItem item) {
			return page.getChildId(item);
		}
		
		@Override
		public StreamItem[] getChildren() {
			return page.getChildren();
		}
		
		@Override
		public StreamItem getParent() {
			return page.getParent();
		}
		
		@Override
		public void setParent(StreamItem parent) {
			page.setParent(parent);
		}
	}
}
