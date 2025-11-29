package de.rcblum.stream.deck.device.descriptor.hidfunctions;

import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

@FunctionalInterface
public interface FeatureReportInterface {
	void sendFeatureReport(HidDevice hidDevice);
}
