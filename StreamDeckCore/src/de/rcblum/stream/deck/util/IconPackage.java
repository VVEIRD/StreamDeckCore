package de.rcblum.stream.deck.util;

import java.awt.image.BufferedImage;

import de.rcblum.stream.deck.items.animation.AnimationStack;
/**
 * Datastructure that pools togerther icon and the associated AnimationStack
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
public class IconPackage {
	
	public final BufferedImage iconSource;
	
	public final BufferedImage[] animationSource;

	/**
	 * Icon that is displayed by default
	 */
	public final byte[] icon;
	
	/**
	 * Animation stack, that contains the necessary frames and framerate/trigger/playback type for the animation.
	 */
	public final AnimationStack animation;

	public IconPackage(byte[] icon, AnimationStack animation, BufferedImage iconSource, BufferedImage[] animationSource) {
		super();
		this.icon = icon;
		this.animation = animation;
		this.iconSource = iconSource;
		this.animationSource = animationSource;
	}
}
