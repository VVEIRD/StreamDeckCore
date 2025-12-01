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
import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.device.general.IStreamDeck;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.FolderItem;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.items.animation.Animator;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;
import de.rcblum.stream.deck.util.SDImage;

public class TestAnimationStack {
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		AnimationStack as = new AnimationStack(AnimationStack.REPEAT_LOOPING, true, AnimationStack.FRAME_RATE_30, AnimationStack.TRIGGER_PRESSED, new SDImage[0]);
		System.out.println(gson.toJson(as));
		IconHelper.createIconPackage("resources" + File.separator + "icon.zip", "resources" + File.separator + "icon.png", "resources" + File.separator + "icon.gif", as);
		IconPackage ip = IconHelper.loadIconPackage("resources" + File.separator + "icon.zip");
		IStreamDeck sd = StreamDeckDevices.getStreamDeck();
		StreamItem[] items = new StreamItem[sd.getKeySize()];
		ExecutableItem item0 = new ExecutableItem(ip, "cmd /c dir");
		ExecutableItem item1 = item0;
		ip = new IconPackage(ip.icon, IconHelper.createRollingTextAnimation(ip.icon.getVariant(sd.getDescriptor().iconSize), "Rolling Text test", StreamItem.TEXT_POS_BOTTOM));
		ExecutableItem item2 = new ExecutableItem(ip, "cmd /c dir");
		
		// Test touch screen animator
		
		BufferedImage touchScreenBI = IconHelper.createResizedCopy(IconHelper.loadRawImage(Paths.get("resources" + File.separator + "lcd" + File.separator + "fantasy_background.png")), false, new Dimension(800, 100));
		SDImage touchScreen = IconHelper.cacheImage("resources" + File.separator + "lcd" + File.separator + "fantasy_background_left_side.png", touchScreenBI);
		ip = new IconPackage(ip.icon, IconHelper.createRollingTextAnimation(touchScreen, "Rolling Text test Rolling Text test Rolling Text test Rolling", StreamItem.TEXT_POS_BOTTOM));

		items[0] = item1;
		items[sd.getColumnSize()-1] = item1;
		items[sd.getColumnSize()*sd.getRowSize()-3] = item2;
		FolderItem root = new FolderItem(null, null, items);
		Animator a = new Animator(sd, 8, ip.animation);
		sd.reset();
		sd.setBrightness(90);
		StreamDeckController sdc = new StreamDeckController(sd, root);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
			Thread.sleep(15_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		item0.setText("Hello");
		sdc.releaseButton(0);
		sdc.releaseButton(4);
		sdc.releaseButton(7);
		sdc.releaseButton(10);
		sdc.releaseButton(14);
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		item0.setTextPosition(StreamItem.TEXT_POS_TOP);
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sd.setBrightness(50);
		item0.setTextPosition(StreamItem.TEXT_POS_CENTER);
		sdc.pressButton(0);
		sdc.pressButton(4);
		sdc.pressButton(7);
		sdc.pressButton(10);
		sdc.pressButton(14);
		try {
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
		System.exit(0);
	}
}
