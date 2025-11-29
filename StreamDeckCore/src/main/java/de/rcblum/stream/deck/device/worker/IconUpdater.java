package de.rcblum.stream.deck.device.worker;

import java.awt.Dimension;

import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawImageInterface;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

/**
 * Sends an Icon to a given key on the ESD.
 * @author Roland von Werden
 *
 */
public class IconUpdater implements Runnable {

	public final Dimension imageSize;
	public final int keyIndex;
	public final SDImage img;
	public final HidDevice hidDevice;
	public final DrawImageInterface drawImageInterface;

	public IconUpdater(HidDevice hidDevice, DrawImageInterface drawImageInterface, int keyIndex, SDImage img, Dimension imageSize) {
		this.keyIndex = keyIndex;
		this.img = img;
		this.hidDevice = hidDevice;
		this.drawImageInterface = drawImageInterface;
		this.imageSize = imageSize;
	}

	@Override
	public void run() {
		drawImageInterface.drawImage(hidDevice, keyIndex, imageSize, img);
	}

}