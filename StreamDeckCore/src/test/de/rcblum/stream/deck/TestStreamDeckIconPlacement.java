package test.de.rcblum.stream.deck;

import java.io.File;
import java.io.IOException;

import de.rcblum.stream.deck.device.IStreamDeck;
import de.rcblum.stream.deck.device.StreamDeckDevices;
import de.rcblum.stream.deck.items.ExecutableItem;
import de.rcblum.stream.deck.util.IconHelper;

/**
 * Tests brightness and all keys of all connected stream deck
 * 
 * MIT License
 * 
 * Copyright (c) 2017 Roland von Werden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author Roland von Werden
 * @version 0.1
 *
 */
public class TestStreamDeckIconPlacement {
	public static void main(String[] args) throws IOException, InterruptedException {
		// Get StreamDeck
		StreamDeckDevices.getStreamDeck();
		for(int i=0; i<StreamDeckDevices.getStreamDeckSize(); i++) {
			IStreamDeck deck = StreamDeckDevices.getStreamDeck(i);
			// Create Executable Key with icon
//			BufferedImage img = ImageIO.read(new File("resources" + File.separator + "icon.png"));
//			img = IconHelper.createResizedCopy(IconHelper.fillBackground(IconHelper.rotate180(img), Color.BLACK));
//			System.out.println(img.getWidth() + ":" + img.getHeight());
			ExecutableItem executableButton = new ExecutableItem(IconHelper.loadImage("resources" + File.separator + "icon.png"), "java.exe");
	
			// Reset stream deck
			deck.reset();
			// set brightness to 25%
			deck.setBrightness(25);
			Thread.sleep(1000);
			// set brightness to 50%
			deck.setBrightness(50);
			Thread.sleep(1000);
			// set brightness to 75%
			deck.setBrightness(75);
			Thread.sleep(1000);
			// set brightness to 99%
			deck.setBrightness(99);
			Thread.sleep(500);
			// Move key through 1 - 15 on stream deck
			deck.addKey(0, executableButton);
			Thread.sleep(500);
			deck.removeKey(0);
			deck.addKey(1, executableButton);
			Thread.sleep(500);
			deck.removeKey(1);
			deck.addKey(2, executableButton);
			Thread.sleep(500);
			deck.removeKey(2);
			deck.addKey(3, executableButton);
			Thread.sleep(500);
			deck.removeKey(3);
			deck.addKey(4, executableButton);
			Thread.sleep(500);
			deck.removeKey(4);
			deck.addKey(5, executableButton);
			Thread.sleep(500);
			deck.removeKey(5);
			deck.addKey(6, executableButton);
			Thread.sleep(500);
			deck.removeKey(6);
			deck.addKey(7, executableButton);
			Thread.sleep(500);
			deck.removeKey(7);
			deck.addKey(8, executableButton);
			Thread.sleep(500);
			deck.removeKey(8);
			deck.addKey(9, executableButton);
			Thread.sleep(500);
			deck.removeKey(9);
			deck.addKey(10, executableButton);
			Thread.sleep(500);
			deck.removeKey(10);
			deck.addKey(11, executableButton);
			Thread.sleep(500);
			deck.removeKey(11);
			deck.addKey(12, executableButton);
			Thread.sleep(500);
			deck.removeKey(12);
			deck.addKey(13, executableButton);
			Thread.sleep(500);
			deck.removeKey(13);
			deck.addKey(14, executableButton);
			Thread.sleep(500);
			deck.removeKey(14);
			deck.reset();
			deck.setBrightness(0);
			deck.waitForCompletion();
			 System.exit(0);
		}
	}
}
