# StreamDeckCore
A java implementation for the Elgato Stream Deck (Called ESD from now on).

I found myself wanting to use a custom app to connect to the ESD using java. Most implementations i found are using javascript. So I used the information form those project to create an java api myself. This is in a very early development state, so it is bound to change a lot.

## Basic functionality
StreamDeckCore provides the following features as of now:
1. Recognizing a connected ESD
2. Resetting the connected ESD
3. Settting the icons of the keys (0 - 14)
4. Setting the brightness of the ESD (0 - 99)
5. Recieving key pressed, released, clicked events from the ESD

## Future functionality
1. Animation on pressed key
2. Custom animations for specific keys, at a decent framrate (60/30/15 fps) if possible.
3. Supporting multiple ESDs
4. Recieving events for key binds that are beeing displayed and when they are taken off (e.g. onDisplay() and offDisplay())

## Dependencies
This uses the github project https://github.com/nyholku/purejavahidapi and jna 4.0, which can be downloaded through maven:

    <!-- https://mvnrepository.com/artifact/net.java.dev.jna/jna -->
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>4.0.0</version>
    </dependency>

## Usage
### Example 1
This example binds one item to on key of the stream deck.
```java
import  de.rcblum.stream.deck.StreamDeck;
import  de.rcblum.stream.deck.StreamDeckDevices;
import  de.rcblum.stream.deck.items.ExecutableItem;
import  de.rcblum.stream.deck.util.IconHelper
    
// Get connected Stream Deck (Only 1 device is supported atm)
StreamDeck deck = StreamDeckDevices.getStreamDeck();

// Reset previous configuration
deck.reset();

// Create a button for index 0, that will start program.exe
BufferedImage img = IconHelper.loadImage("resources" + File.separator + "icon.png");
ExecutableItem ExecutableItem = new ExecutableItem(0, img,"program.exe");

// Register key to index 0 with Stream Deck
deck.addKey(0, ExecutableItem);
```
### Example 2
This example creates 15 items, puts them into a folder and put that folder into another folder, which acts as root folder. The root folder and the StreamDeck device is given to the StreamDeckController, which handles traversing the given folders and handing over KeyEvents to non-folder items.
```java
import  de.rcblum.stream.deck.StreamDeck;
import  de.rcblum.stream.deck.StreamDeckController;
import  de.rcblum.stream.deck.StreamDeckDevices;
import  de.rcblum.stream.deck.items.FolderItem;
import  de.rcblum.stream.deck.items.ExecutableItem;

// Get one of the connected stream devices
StreamDeck sd = StreamDeckDevices.getStreamDeck();

// Reset device
sd.reset();

// Set brightness to 50%
sd.setBrightness(50);

// Create 15 items
StreamItem[] items = new StreamItem[15];
for (int i = 0; i < items.length; i++) {
	System.out.println("resources" + File.separator + "icon" + (i+1) + ".png");
	byte[] icon = IconHelper.loadImage("resources" + File.separator + "icon" + (i+1) + ".png");
	ExecutableItem eI = new ExecutableItem(icon, "explorer");
	items[i] = eI;
}

// Put items into folder
FolderItem dir = new FolderItem("Folder Level 1", null, items);

// Put folder with items in root folder
StreamItem[] rootDirs = new StreamItem[15];
rootDirs[4] = dir;
FolderItem root = new FolderItem(null, null, rootDirs);

// Create stream deck controller that will
// handle folders and events sent from the stream deck
StreamDeckController controller = new StreamDeckController(sd, root);
```


