package de.rcblum.stream.deck.items;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.util.IconHelper;

public abstract class ToggleItem extends AbstractStreamItem {
	
	byte[] unmodded = null;
	
	boolean isOn = false;

	public ToggleItem() {
		super(IconHelper.BLACK_ICON);
		this.unmodded = this.rawImg;
	}

	public ToggleItem(boolean selected) {
		super(IconHelper.BLACK_ICON);
		this.unmodded = this.rawImg;
		this.isOn = selected;
		this.updateIcon();
	}

	public ToggleItem(byte[] icon, boolean selected) {
		super(icon);
		this.unmodded = icon;
		this.isOn = selected;
		this.updateIcon();
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
		switch (event.getType()) {
		// Trigger onDisplay Method for custom init code
		case ON_DISPLAY:
			this.onDisplay();
			break;
		// Toggle between on and off and call apropriate method.
		case RELEASED_CLICKED:
			this.isOn = !this.isOn;
			updateIcon();
			if (this.isOn) 
				onEnable(true);
			else 
				onDisable(true);
			break;
		default:
			break;
		}
	}

	private void updateIcon() {
		if (this.isOn) {
			this.rawImg = IconHelper.applyImage(this.unmodded, IconHelper.getImageFromResource("/resources/selected.png"));
		}
		else {
			this.rawImg = this.unmodded;
		}
		this.img = this.text != null ? IconHelper.addText(this.rawImg, this.text, this.textPos) : this.rawImg;
		this.fireIconUpdate();
	}
	
	public void setSelected(boolean selected) {
		if (selected != this.isOn) {
			this.isOn = selected;
			updateIcon();
			if(this.isOn)
				onEnable(false);
			else
				onDisable(false);
		}
	}
	
	public boolean isSelected() {
		return isOn;
	}

	/**
	 * Is called when the Item is brought to display
	 */
	protected abstract void onDisplay();
	
	/**
	 * Is called when the Item is toggled to on.
	 */
	protected abstract void onEnable(boolean byEvent);
	
	/**
	 * Is called when the item is toggled to off
	 */
	protected abstract void onDisable(boolean byEvent);

}
