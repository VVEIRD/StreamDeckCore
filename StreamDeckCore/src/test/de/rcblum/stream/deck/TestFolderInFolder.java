package test.de.rcblum.stream.deck;

import java.io.File;
import java.io.IOException;

import de.rcblum.stream.deck.StreamDeck;
import de.rcblum.stream.deck.StreamDeckController;
import de.rcblum.stream.deck.StreamDeckDevices;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.items.FolderStreamItem;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;

public class TestFolderInFolder {
	public static void main(String[] args) throws IOException {
		
		StreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.reset();
		sd.setBrightness(50);
		StreamItem[] items = new StreamItem[15];
		for (int i = 0; i < items.length; i++) {
			System.out.println("resources" + File.separator + "icon" + (i+1) + ".png");
			byte[] icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "explorer");
			items[i] = eI;
		}
		FolderStreamItem dir = new FolderStreamItem(null, items);
		StreamItem[] rootDirs = new StreamItem[15];
		rootDirs[4] = dir;
		FolderStreamItem root = new FolderStreamItem(null, rootDirs);
		
		StreamDeckController controller = new StreamDeckController(sd, root);
		try {
			Thread.sleep(60_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sd.setBrightness(0);
		System.exit(0);
	}
}
