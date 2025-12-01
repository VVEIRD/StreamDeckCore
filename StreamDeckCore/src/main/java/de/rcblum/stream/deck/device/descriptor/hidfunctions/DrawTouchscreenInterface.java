package de.rcblum.stream.deck.device.descriptor.hidfunctions;

import java.awt.Dimension;
import java.awt.Point;

import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

@FunctionalInterface
public interface DrawTouchscreenInterface {
	void drawImage(HidDevice hidDevice, Point imageStart, Dimension imageDimension, SDImage imgData);
}
