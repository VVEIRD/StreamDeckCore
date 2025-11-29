package de.rcblum.stream.deck.device.worker;

import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportInterface;
import purejavahidapi.HidDevice;

/**
 * Sends the reset command to the ESD.
 * @author Roland von Werden
 *
 */
public class Resetter implements Runnable {
	
	HidDevice hidDevice;
	
	FeatureReportInterface resetInterface;
	
	public Resetter(HidDevice hidDevice, FeatureReportInterface resetInterface) {
		this.hidDevice = hidDevice;
		this.resetInterface = resetInterface;
	}

	@Override
	public void run() {
		this.resetInterface.sendFeatureReport(this.hidDevice);
	}
}