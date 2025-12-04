package test.de.rcblum.stream.deck;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.animation.AnimationStack;
import de.rcblum.stream.deck.animation.Animator;
import de.rcblum.stream.deck.device.StreamDeckConstants;
import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.descriptor.KeyType;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.FolderItem;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;
import de.rcblum.stream.deck.util.SDImage;

public class TestAnimationStack {
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());

		IStreamDeck sd  = StreamDeckDevices.getStreamDeck();
		
		// Create an Icon Package containing 
		// 1. The animation configuration
		// 2. The images from an GIF as the animation frames
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		AnimationStack as = new AnimationStack(AnimationStack.REPEAT_LOOPING, true, AnimationStack.FRAME_RATE_30, AnimationStack.TRIGGER_PRESSED, new SDImage[0]);
		System.out.println(gson.toJson(as));
		// Create and load an icon package zip file
		IconPackage ipA = IconHelper.createIconPackage("resources" + File.separator + "icon.zip", "resources" + File.separator + "icon.png", "resources" + File.separator + "icon.gif", as, sd.getDescriptor().iconSize, true);
		// Load an existing icon package zip file
		IconPackage ipB = IconHelper.loadIconPackage("resources" + File.separator + "icon.zip", sd.getDescriptor().iconSize);
		// Create an IconPackage with a rolling text animation
		IconPackage ipC = new IconPackage(IconHelper.convertImage(ipA.icon.image), IconHelper.createRollingTextAnimation(ipA.icon.getVariant(sd.getDescriptor().iconSize), "Rolling Text test", StreamItem.TEXT_POS_BOTTOM));

		// Create StreamItems to be displayed with the Stream Deck Controller
		StreamItem[] items = new StreamItem[sd.getKeySize()];
		ExecutableItem item0 = new ExecutableItem(ipA, "echo \"Item 0 pressed\"");
		ExecutableItem item1 = new ExecutableItem(ipB, "echo \"Item 1 pressed\"");
		ExecutableItem item2 = new ExecutableItem(ipC, "cmd /c dir");
		ExecutableItem item3 = new ExecutableItem(ipB.icon.copy(), "cmd /c dir");

		items[0]                                    = item0;
		items[sd.getColumnSize()-1]                 = item1;
		items[sd.getColumnSize()*sd.getRowSize()-3] = item2;
		items[sd.getColumnSize()*sd.getRowSize()-2] = item3;
		FolderItem root = new FolderItem(null, null, items);

		SDImage touchScreenImage = IconHelper.loadImage("resources" + File.separator + "lcd" + File.separator + "fantasy_background.png");
		System.out.println("Touch screen dimensions: " + touchScreenImage.imageSize);
		if(sd.hasTouchScreen()) {
			AnimationStack aStack = IconHelper.createRollingTextAnimation(touchScreenImage, "Rolling Text Test Rolling Text Test Rolling Text Test Rolling Text Test", StreamItem.TEXT_POS_BOTTOM);
			sd.getTouchScreen().addTouchScreenAnimation(aStack);
		}
		sd.reset();
		sd.setBrightness(90);
		StreamDeckController sdc = new StreamDeckController(sd, root);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
			Thread.sleep(5_000);
			if(sd.hasTouchScreen()) {
				AnimationStack aStack = IconHelper.createRollingTextAnimation(touchScreenImage, "Rolling Text Test Rolling Text Test Rolling Text Test Rolling Text Test", StreamItem.TEXT_POS_TOP);
				ipC.animation.setTextPos(StreamItem.TEXT_POS_TOP);
				sd.getTouchScreen().addTouchScreenAnimation(aStack);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		System.out.println(IconHelper.DEFAULT_FONT);
		item0.setText("Center", StreamItem.TEXT_POS_CENTER);
		item3.setText("Center", StreamItem.TEXT_POS_CENTER);
		
		System.out.println(IconHelper.DEFAULT_FONT);
		sdc.releaseButton(0);
		sdc.releaseButton(4);
		sdc.releaseButton(7);
		sdc.releaseButton(10);
		sdc.releaseButton(14);
		try {
			Thread.sleep(5_000);
			if(sd.hasTouchScreen()) {
				AnimationStack aStack = IconHelper.createRollingTextAnimation(touchScreenImage, "Rolling Text Test Rolling Text Test Rolling Text Test Rolling Text Test", StreamItem.TEXT_POS_TOP);
				sd.getTouchScreen().addTouchScreenAnimation(aStack);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		item0.setText("Top", StreamItem.TEXT_POS_TOP);
		item3.setText("Top", StreamItem.TEXT_POS_TOP);
		try {

			Thread.sleep(2_000);
			System.out.println("Drawing item0 on other key");
			sd.drawImage(sd.getColumnSize()*sd.getRowSize()-1, item0.getIcon());
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sd.setBrightness(50);
		if(sd.hasTouchScreen()) {
			AnimationStack aStack = IconHelper.createRollingTextAnimation(touchScreenImage, "Rolling Text Test Rolling Text Test Rolling Text Test Rolling Text Test", StreamItem.TEXT_POS_CENTER);
			sd.getTouchScreen().addTouchScreenAnimation(aStack);
		}
		item0.setText("Bottom", StreamItem.TEXT_POS_BOTTOM);
		item3.setText("Bottom", StreamItem.TEXT_POS_BOTTOM);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sd.setBrightness(50);
		item0.setText("Center Center Center", StreamItem.TEXT_POS_CENTER);
		item1.setText("ASDFGH ASDFGH ASDFGH", StreamItem.TEXT_POS_CENTER);
		item1.getAnimation().setTrigger(AnimationStack.TRIGGER_AUTO);
		item3.setText("Center Center Center", StreamItem.TEXT_POS_CENTER);
		try {
			Thread.sleep(2_000);
			sd.drawImage(sd.getColumnSize()*sd.getRowSize()-2, item3.getIcon());
			Thread.sleep(90_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sdc.releaseButton(7);
		sd.reset();
		sd.setBrightness(0);
		sdc.stop(true, true);
		sd.waitForCompletion();
		sd.reset();
		Thread.sleep(200);
		System.exit(0);
	}
}
