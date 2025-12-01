package de.rcblum.stream.deck.device.worker;

import java.awt.Dimension;
import java.awt.Point;

import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawImageInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.DrawTouchscreenInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportIntegerInterface;
import de.rcblum.stream.deck.device.descriptor.hidfunctions.FeatureReportInterface;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

/**
 * Sends an Icon to a given key on the ESD.
 * @author Roland von Werden
 *
 */
public class DeckUpdater implements Runnable {

	public final Point startPoint;
	public final Dimension imageSize;
	public final int keyIndex;
	public final SDImage img;
	public final HidDevice hidDevice;
	public final DrawImageInterface drawImageInterface;
	public final DrawTouchscreenInterface drawTouchscreenInterface;
	
	public final FeatureReportInterface featureReportInterface;
	public final FeatureReportIntegerInterface featureReportIntInterface;
	public final int brightness;

	public DeckUpdater(HidDevice hidDevice, DrawImageInterface drawImageInterface, int keyIndex, SDImage img, Dimension imageSize) {
		this.keyIndex = keyIndex;
		this.img = img;
		this.hidDevice = hidDevice;
		this.drawImageInterface = drawImageInterface;
		this.drawTouchscreenInterface = null;
		this.featureReportInterface = null;
		this.featureReportIntInterface = null;
		this.brightness = -1;
		this.imageSize = imageSize;
		this.startPoint = null;
	}

	public DeckUpdater(HidDevice hidDevice, DrawTouchscreenInterface drawTouchIface, Point startPoint, SDImage img, Dimension imageSize) {
		this.keyIndex = -1;
		this.img = img;
		this.hidDevice = hidDevice;
		this.drawImageInterface = null;
		this.drawTouchscreenInterface = drawTouchIface;
		this.featureReportInterface = null;
		this.featureReportIntInterface = null;
		this.brightness = -1;
		this.imageSize = imageSize;
		this.startPoint = startPoint;
	}
	
	public DeckUpdater(HidDevice hidDevice, FeatureReportInterface featureReportInterface) {
		this.hidDevice = hidDevice;
		this.featureReportInterface = featureReportInterface;
		this.keyIndex = -1;
		this.img = null;
		this.drawImageInterface = null;
		this.drawTouchscreenInterface = null;
		this.featureReportIntInterface = null;
		this.brightness = -1;
		this.imageSize = null;
		this.startPoint = null;
	}

	
	public DeckUpdater(HidDevice hidDevice, FeatureReportIntegerInterface featureReportIntInterface, int brightness) {
		this.hidDevice = hidDevice;
		this.featureReportIntInterface = featureReportIntInterface;
		this.featureReportInterface = null;
		this.drawImageInterface = null;
		this.drawTouchscreenInterface = null;
		this.brightness = brightness;
		this.keyIndex = -1;
		this.img = null;
		this.imageSize = null;
		this.startPoint = null;
	}

	@Override
	public void run() {
		if (this.drawImageInterface != null)
			drawImageInterface.drawImage(hidDevice, keyIndex, imageSize, img);
		else if (this.featureReportInterface != null)
			this.featureReportInterface.sendFeatureReport(this.hidDevice);
		else if (this.featureReportIntInterface != null)
			this.featureReportIntInterface.sendFeatureReportInt(this.hidDevice, this.brightness);
		else if (this.drawTouchscreenInterface != null)
			drawTouchscreenInterface.drawImage(hidDevice, startPoint, imageSize, img);
	}

}