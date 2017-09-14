package test.de.rcblum.stream.deck;

import java.io.File;
import java.io.IOException;

import de.rcblum.stream.deck.StreamDeck;
import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.StreamDeckDevices;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.FolderItem;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;

public class TestFolderInFolder {
	public static void main(String[] args) throws IOException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		StreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.reset();
		sd.setBrightness(50);
		// Level 2
		StreamItem[] items = new StreamItem[15];
		for (int i = 0; i < items.length; i++) {
			byte[] icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "explorer");
			items[i] = eI;
		}
		FolderItem dir = new FolderItem("Folder Level 2", null, items);
		
		// Level 1
		items = new StreamItem[15];
		items[0] = dir;
		for (int i = 1; i < items.length; i++) {
			byte[] icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "explorer");
			items[i] = eI;
		}
		dir = new FolderItem("Folder Level 1", null, items);
		// Root
		StreamItem[] rootDirs = new StreamItem[15];
		rootDirs[0] = dir;
		rootDirs[4] = dir;
		rootDirs[6] = dir;
		rootDirs[8] = dir;
		rootDirs[12] = dir;
		FolderItem root = new FolderItem(null, null, rootDirs);
		
		StreamDeckController controller = new StreamDeckController(sd, root);
		try {
			Thread.sleep(60_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sd.setBrightness(0);
		sd.waitForCompletion();
		System.exit(0);
	}
}
