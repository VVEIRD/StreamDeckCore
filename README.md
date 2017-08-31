# StreamDeckCore
A java implementation for the Elgato Stream Deck.

I found myself wanting to use a custom app to connect to the Elgato Stream Deck using java. Most implementations i found use javascript. so I used the information form those project to create a api myself. This is in a very early development state, so it is bound to change a lot.

## Dependencies
This uses the github project https://github.com/nyholku/purejavahidapi and jna 4.0, which can be downloaded through maven:

    <!-- https://mvnrepository.com/artifact/net.java.dev.jna/jna -->
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>4.0.0</version>
    </dependency>

## Usage example
```java
import  de.rcblum.stream.deck.*;
    
// Get connected Stream Deck (Only 1 device is supported atm)
StreamDeck deck = HidDevices.getStreamDeck();

// Reset previous configuration
deck.reset();

// Create a button for index 0, that will start program.exe
BufferedImage img = ImageIO.read(new File("resources" + File.separator + "icon.png"));
img = IconHelper.createResizedCopy(IconHelper.fillBackground(IconHelper.rotate180(img), Color.BLACK));
ExecutableButton executableButton = new ExecutableButton(0, img,"program.exe");

// Register key to index 0 with Stream Deck
deck.addKey(0, executableButton);
```
