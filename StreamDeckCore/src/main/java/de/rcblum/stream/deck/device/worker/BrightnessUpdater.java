package de.rcblum.stream.deck.device.worker;

import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportIntegerInterface;
import purejavahidapi.HidDevice;

/**
 * Job that is submitted, when the Method {@link StreamDeck#setBrightness(int)} is called.<br>
 * When executed it will call the Method {@link StreamDeck#internalUpdateBrightnes()}, which will send the brightness command to the stream deck.
 * @author Roland von Werden
 *
 */
public class BrightnessUpdater implements Runnable {
	
	HidDevice hidDevice;
	FeatureReportIntegerInterface brightnessInterface;
	int brightness;
	
	public BrightnessUpdater(HidDevice hidDevice, FeatureReportIntegerInterface brightnessInterface, int brightness) {
		this.hidDevice = hidDevice;
		this.brightnessInterface = brightnessInterface;
		this.brightness = brightness;
	}

	@Override
	public void run() {
		this.brightnessInterface.sendFeatureReportInt(this.hidDevice, this.brightness);
	}
}