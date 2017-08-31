package de.rcblum.stream.deck;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This  handle can be registered with the {@link StreamDeck} and will execute
 * the given executable when the stream deck button is pressed on release.
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
public class ExecutableItem implements StreamItem {
	
	int id = -1;
	
	BufferedImage img = null;

	String pathToExecutable = null;

	public ExecutableItem(int keyIndex, BufferedImage img, String pathToExecutable) {
		super();
		this.id = keyIndex;
		this.img = img;
		this.pathToExecutable = pathToExecutable;
	}

	@Override
	public int getKeyIndex() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public BufferedImage getIcon() {
		// TODO Auto-generated method stub
		return this.img;
	}

	@Override
	public void onClick() {
		System.out.println(id +": Click");
	}

	@Override
	public void onPress() {
		System.out.println(id +": Press");
	}

	@Override
	public void onRelease() {
		System.out.println(id +": Release");
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(this.pathToExecutable);
		} catch (IOException e) {
			System.out.println(id +": Could nod execute " + this.pathToExecutable);
			e.printStackTrace();
		}
	}

	@Override
	public void onDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offDisplay() {
		// TODO Auto-generated method stub
		
	}

}
