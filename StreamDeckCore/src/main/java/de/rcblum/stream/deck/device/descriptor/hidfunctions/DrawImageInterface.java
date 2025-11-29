package de.rcblum.stream.deck.device.descriptor.hidfunctions;

import java.awt.Dimension;

import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

@FunctionalInterface
public interface DrawImageInterface {
	void drawImage(HidDevice hidDevice, int keyIndex, Dimension iconSize, SDImage imgData);
}
