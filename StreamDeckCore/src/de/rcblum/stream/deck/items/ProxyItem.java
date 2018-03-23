package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.util.IconHelper;

public class ProxyItem extends FolderItem {

		private StreamItem wrapped = null;

		private String line1 = null;
		
		private int line1FontSize = 16;

		private String line2 = null;
		
		private int line2FontSize = 16;

		private String line3 = null;
		
		private int line3FontSize = 16;
		
		public ProxyItem(StreamItem wrapped, String text) {
			super(text, null, new StreamItem[15]);
			this.wrapped = wrapped;
			this.setTextPosition(TEXT_POS_CENTER);
			this.setText(text);
		}
		
		@Override
		public StreamItem getChild(int i) {
			return wrapped.getChild(i);
		}
		
		@Override
		public AnimationStack getAnimation() {
			return wrapped.getAnimation();
		}
		
		@Override
		public int getChildId(StreamItem item) {
			return wrapped.getChildId(item);
		}
		
		@Override
		public StreamItem[] getChildren() {
			return wrapped.getChildren();
		}
		
		@Override
		public StreamItem getParent() {
			return wrapped.getParent();
		}
		
		@Override
		public void setParent(StreamItem parent) {
			wrapped.setParent(parent);
		}

		@Override
		public void setIcon(byte[] icon) {
			this.rawImg = icon;
			this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
			if (line1 != null)
				this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP);
			if (line2 != null)
				this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER);
			if (line3 != null)
				this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM);
			this.fireIconUpdate();
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
				if (line1 != null)
					this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP);
				if (line2 != null)
					this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER);
				if (line3 != null)
					this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM);
				this.fireIconUpdate();
			}
		}

		public void setTextPosition(int textPos) {
			this.textPos = textPos;
			if (this.text != null) {
				this.img = IconHelper.addText(this.rawImg, this.text, this.textPos);
				if (line1 != null)
					this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP);
				if (line2 != null)
					this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER);
				if (line3 != null)
					this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM);
				if (this.animation != null) {
					this.animation.setTextPos(this.textPos);
				}
				this.fireIconUpdate();
			}
		}
		
		public void setTextLine1(String text, int fontSize) {
			line1 = text;
			line1FontSize = fontSize;
			this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
			if (line1 != null)
				this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP, line1FontSize);
			if (line2 != null)
				this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER, line2FontSize);
			if (line3 != null)
				this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM, line3FontSize);
		}
		
		public void setTextLine2(String text, int fontSize) {
			line2 = text;
			line2FontSize = fontSize;
			this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
			if (line1 != null)
				this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP, line1FontSize);
			if (line2 != null)
				this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER, line2FontSize);
			if (line3 != null)
				this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM, line3FontSize);
		}

		
		public void setTextLine3(String text, int fontSize) {
			line3 = text;
			line3FontSize = fontSize;
			this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
			if (line1 != null)
				this.img = IconHelper.addText(this.img, line1, TEXT_POS_TOP, line1FontSize);
			if (line2 != null)
				this.img = IconHelper.addText(this.img, line2, TEXT_POS_CENTER, line2FontSize);
			if (line3 != null)
				this.img = IconHelper.addText(this.img, line3, TEXT_POS_BOTTOM, line3FontSize);
		}
	}