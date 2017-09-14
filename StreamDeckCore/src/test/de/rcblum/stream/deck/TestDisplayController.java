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

public class TestDisplayController {
	public static void main(String[] args) throws IOException {
		System.setProperty("log4j.configurationFile", TestAnimationStack.class.getResource("/resources/log4j.xml").getFile());
		StreamDeck sd = StreamDeckDevices.getStreamDeck();
		sd.reset();
		sd.setBrightness(100);
		StreamItem[] items = new StreamItem[15];
		for (int i = 0; i < items.length; i++) {
			byte[] icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
			ExecutableItem eI = new ExecutableItem(icon, "explorer");
			items[i] = eI;
		}
		FolderItem root = new FolderItem("Testfolder", null, items);
		
		StreamDeckController controller = new StreamDeckController(sd, root);
	}
}
