package de.rcblum.stream.deck.event;

import java.util.EventObject;

import de.rcblum.stream.deck.StreamDeck;

/**
 * Event that represents an event triggered by interacting with the stream deck.
 * 
 * <br><br> 
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
public class KeyEvent extends EventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 82319643971116963L;
	
	/**
	 * Id of the key
	 */
	private int keyId;
	
	/**
	 * Type of the event
	 */
	private Type type;

	public KeyEvent(StreamDeck source, int keyId, Type type) {
		super(source);
		this.keyId =  keyId;
		this.type = type;
	}

	@Override
	public StreamDeck getSource() {
		return (StreamDeck)super.getSource();
	}
	
	public int getKeyId() {
		return keyId;
	}
	
	public Type getType() {
		return type;
	}
	
	public static enum Type {
		PRESSED, RELEASED_CLICKED, ON_DISPLAY, OFF_DISPLAY, OPEN_FOLDER, CLOSE_FOLDER
	}
}
