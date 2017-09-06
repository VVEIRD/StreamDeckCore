package test.de.rcblum.stream.deck;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rcblum.stream.deck.StreamDeck;
import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.StreamDeckDevices;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.FolderItem;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.items.animation.AnimationStack;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.IconPackage;

public class TestAnimationStack {
	public static void main(String[] args) throws URISyntaxException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		AnimationStack as = new AnimationStack(AnimationStack.REPEAT_LOOPING, AnimationStack.FRAME_RATE_30, AnimationStack.TRIGGER_AUTO, new byte[0][0]);
		System.out.println(gson.toJson(as));
		IconHelper.createIconPackage("resources" + File.separator + "icon.zip", "resources" + File.separator + "icon00.gif", "resources" + File.separator + "icon.gif", as);
		IconPackage ip = IconHelper.loadIconPackage("resources" + File.separator + "icon.zip");
		StreamItem[] items = new StreamItem[15];
		ExecutableItem item0 = new ExecutableItem(IconHelper.loadImage("resources" + File.separator + "icon00.gif"), "notepad");
		item0.setIconPackage(ip);
		items[7] = item0;
		FolderItem root = new FolderItem(null, null, items);
		StreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.reset();
		sd.setBrightness(90);
		StreamDeckController sdc = new StreamDeckController(sd, root);

		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		item0.setText("Hello");
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		item0.setTextPosition(StreamItem.TEXT_POS_TOP);
		try {
			Thread.sleep(15_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sd.setBrightness(5);
		sd.reset();
		sd.stop();
		sd.waitForCompletion();
		System.exit(0);
	}
}
