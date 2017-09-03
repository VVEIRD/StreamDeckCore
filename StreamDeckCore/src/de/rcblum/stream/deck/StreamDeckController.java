package de.rcblum.stream.deck;

import java.util.Objects;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.items.StreamItem;

/**
 * Can be used to hand over control over the stream deck by providing a "folder" structure. Folders will be traversed automatically, KeyEvents will be forwarded to non folder {@link StreamItem}s.   
 * 
 * <br>
 * <br>
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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
public class StreamDeckController implements StreamKeyListener {

	public StreamDeckController(StreamDeck streamDeck, StreamItem root) {
		super();
		this.streamDeck = streamDeck;
		this.streamDeck.addKeyListener(this);
		this.root = root;
		while (this.root.getParent() != null)
			this.root = this.root.getParent();
		this.currentDir = root;
		this.updateDisplay();
	}

	private StreamDeck streamDeck = null;

	private StreamItem root = null;

	private StreamItem currentDir = null;

	@Override
	public void onKeyEvent(KeyEvent event) {
		StreamItem[] children = this.currentDir.getChildren();
		int id = event.getKeyId();
		Type type = event.getType();
		if (id == 4 && this.currentDir.getParent() != null && type == Type.RELEASED_CLICKED) {
			openFolder(this.currentDir.getParent());
		}
		else if (children[id] != null && !children[id].isLeaf() && type == Type.RELEASED_CLICKED){
			openFolder(children[id]);
		}
		else if (children[id] != null && !(id == 4 && this.currentDir.getParent() != null))
			children[id].onKeyEvent(event);
			
	}

	private void openFolder(StreamItem folder) {
		folder = Objects.requireNonNull(folder);
		if (!folder.isLeaf() && this.currentDir != folder) {
			this.currentDir = folder;
			this.updateDisplay();
		}
	}

	private void updateDisplay() {
		StreamItem[] children = this.currentDir.getChildren();
		if (children != null)
			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					System.out.println("Drawing " + i);
					if (this.currentDir.getParent() != null && i == 4) {
						streamDeck.drawImage(i, this.currentDir.getIcon());						
					}
					else {
						streamDeck.drawImage(i, children[i].getIcon());
					}
				}
				else {
					streamDeck.clearButton(i);
				}
			}
	}

}
