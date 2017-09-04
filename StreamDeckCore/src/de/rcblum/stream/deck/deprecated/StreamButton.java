package de.rcblum.stream.deck.deprecated;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import purejavahidapi.HidDevice;

/**
 * 
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
@Deprecated
public class StreamButton {
	public final static int BUTTON_COUNT = 15;
	
	public final static int ICON_SIZE = 72;

	public final static int PAGE_PACKET_SIZE = 8190;
	
	public final static int NUM_FIRST_PAGE_PIXELS = 2583;
	
	public final static int NUM_SECOND_PAGE_PIXELS = 2601;

	private static byte[] headerTemplatePage1 = new byte[] {
			0x01, 0x01, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x42, 0x4D,
			(byte)0xF6, 0x3C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x48, 0x00,
			0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xC0, 0x3C, 0x00, 0x00,
			(byte)0xC4, 0x0E, 0x00, 0x00, (byte)0xC4, 0x0E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00
        };

        private static byte[] headerTemplatePage2 = new byte[] {
        	0x01, 0x02, 0x00, 0x01, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
	
	
	
	private int buttenNo = 4;
	
	private Color background = Color.BLACK;
	
	private HidDevice streamDeck = null;
	
	private BufferedImage canvas = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);

	public StreamButton(int buttenNo, Color background, HidDevice streamDeck) {
		super();
		this.buttenNo = buttenNo;
		this.background = background;
		this.streamDeck = streamDeck;
//		for (int i = 0; i < PAGE_1.length; i++) {
//			PAGE_1[i] = 0;
//		}
//		for (int i = 0; i < PAGE_1_HEADER.length; i++) {
//			PAGE_1[i] = PAGE_1_HEADER[i];
//		}
//		for (int i = 0; i < PAGE_2.length; i++) {
//			PAGE_2[i] = 0;
//		}
//		for (int i = 0; i < PAGE_2_HEADER.length; i++) {
//			PAGE_2[i] = PAGE_2_HEADER[i];
//		}
	}
	
	
	public void drawImage(BufferedImage img) {
		Graphics2D g = canvas.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
		if (img != null)
			g.drawImage(img, 0, 0, null);
		g.dispose();
		int[] pixels = ((DataBufferInt) canvas.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[ICON_SIZE * ICON_SIZE * 3];
		int imgDataCount=0;
		// remove the alpha channel
		for(int i=0;i<ICON_SIZE*ICON_SIZE; i++) {
			//RGB -> BGR
			imgData[imgDataCount++] = (byte)((pixels[i]>>16) & 0xFF);
			imgData[imgDataCount++] = (byte)(pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte)((pixels[i]>>8) & 0xFF);			
		}
		byte[] page1 = generatePage1(this.buttenNo, imgData);
		byte[] page2 = generatePage2(this.buttenNo, imgData);
		System.out.println(page1.length);
		System.out.println(page2.length);
		System.out.println("Write page 1");
		System.out.println(this.streamDeck.setOutputReport((byte)0x02, page1, page1.length));
		System.out.println("Write page 2");
		System.out.println(this.streamDeck.setOutputReport((byte)0x02, page2, page2.length));
//		_writePage2(imgDataPage2);
		System.out.println("Done 1");
		
	}
	
	private static byte[] generatePage1(int keyId, byte[] imgData)
    {
        byte[] p1 = new byte[PAGE_PACKET_SIZE];
        for (int i = 0; i < headerTemplatePage1.length; i++) {
			p1[i] = headerTemplatePage1[i];
		}
        if (imgData != null) {
            byte[] imgDataPage1 = Arrays.copyOf(imgData, NUM_FIRST_PAGE_PIXELS * 3);
            for (int i = 0; i < imgDataPage1.length; i++) {
				p1[headerTemplatePage1.length+i] = imgDataPage1[i];
			}
        }
        p1[4] = (byte)(keyId + 1);
        return p1;
    }
	
	private static byte[] generatePage2(int keyId, byte[] imgData)
    {
        byte[] p2 = new byte[PAGE_PACKET_SIZE];
        for (int i = 0; i < headerTemplatePage2.length; i++) {
			p2[i] = headerTemplatePage2[i];
		}
        if (imgData != null) {
            byte[] imgDataPage2 = Arrays.copyOfRange(imgData, NUM_FIRST_PAGE_PIXELS * 3, (NUM_FIRST_PAGE_PIXELS * 3) + (NUM_SECOND_PAGE_PIXELS * 3));
            for (int i = 0; i < imgDataPage2.length; i++) {
				p2[headerTemplatePage2.length+i] = imgDataPage2[i];
			}
        }
        p2[4] = (byte)(keyId + 1);
        return p2;
    }

}
