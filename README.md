# StreamDeckCore
A java implementation for the Elgato Stream Deck (Called ESD from now on).

I found myself wanting to use a custom app to connect to the ESD using java. Most implementations i found are using javascript. So I used the information form those project to create an java api myself. This is in a very early development state, so it is bound to change a lot.

## Basich functionality
StreamDeckCore provides the following featers as of now:
1. Recognizing a connected ESD
2. Resetting the connected ESD
3. Settting the icons of the keys (0 - 14)
4. Setting the brightness of the ESD (0 - 99)
5. Recieving key pressed, released, clicked events from the ESD

## Future functionality
1. Animation on pressed key
2. Custom animations for specific key,s at a decent framrate (60/30/15 fps) if possible.
3. Supporting multiple ESDs

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
