package de.rcblum.stream.deck.device.descriptor;

import java.awt.Dimension;

public class KeyType {
	
	public static final int BUTTON_T         = 1;
	
	public static final int IMAGE_BUTTON_T   = 2;
	
	public static final int ROTARY_ENCODER_T = 3;
	
	public static final int TOUCH_SCREEN_T   = 4;
	
	public static KeyType BUTTON = new KeyType(BUTTON_T, 0, 0, true);
	public static KeyType IMAGE_BUTTON = new KeyType(IMAGE_BUTTON_T, 72, 72, false);
	public static KeyType ROTARY_ENCODER = new KeyType(ROTARY_ENCODER_T, 0, 0, true);
	public static KeyType TOUCH_SCREEN = new KeyType(TOUCH_SCREEN_T, 800, 100, true);
	
	private final int type;

	private int dimensionX;
	private int dimensionY;
	private boolean rowless;
	
	private KeyType(int type, int dimensionX, int dimensionY, boolean rowless) {
		this.type = type;
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		this.rowless = rowless;
	}

	public int getDimensionX() {
		return dimensionX;
	}

	public void setDimensionX(int dimensionX) {
		this.dimensionX = dimensionX;
	}

	public int getDimensionY() {
		return dimensionY;
	}

	public void setDimensionY(int dimensionY) {
		this.dimensionY = dimensionY;
	}

	public boolean isRowless() {
		return rowless;
	}

	public void setRowless(boolean rowless) {
		this.rowless = rowless;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KeyType) 
			return this.type == ((KeyType)obj).type;
		return super.equals(obj);
	}
	
	public KeyType variant(Dimension dim) {
		return new KeyType(this.type, (int)dim.getWidth(), (int)dim.getHeight(), this.rowless);
	}

	public Dimension getDimension() {
		return new Dimension(dimensionX, dimensionY);
	}
	
}
