package de.rcblum.stream.deck.device.descriptor;

public enum KeyType {
	
	BUTTON(0, 0, true), IMAGE_BUTTON(72, 72, false), ROTARY_ENCODER(0, 0, true), TOUCH_SCREEN(72, 360, true);

	public final int dimensionX;
	public final int dimensionY;
	public final boolean rowless;
	
	private KeyType(int dimensionX, int dimensionY, boolean rowless) {
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		this.rowless = rowless;
	}
	
}
