package de.rcblum.stream.deck.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

public class SoftStreamDeck implements IStreamDeck {

	private String name = null;
		
	private IStreamDeck streamDeck = null;
	
	StreamItem[] keys = new StreamItem[15];
	
	private List<StreamKeyListener> listerners;
	
	private JFrame frame = null;

	private BufferedImage writeBuffer = null;
	
	private BufferedImage drawBuffer = null;
	
	private Graphics2D drawGraphics = null;

	private Thread writeThread = null;
	
	private Timer drawThread = null;
	
	private boolean run = true;
	
	private ConcurrentLinkedQueue<IconUpdate> updateQueue = new ConcurrentLinkedQueue<>();

	
	public SoftStreamDeck(String name, IStreamDeck streamDeck) {
		this.streamDeck = streamDeck;
		listerners = new ArrayList<>(4);
		this.writeBuffer = IconHelper.getImageFromResource("/resources/sd-background.png");
		this.drawBuffer = IconHelper.getImageFromResource("/resources/sd-background.png");
		this.name = name;
		this.drawGraphics = this.drawBuffer.createGraphics();
		this.frame = new JFrame(name);
		this.frame.setSize(new Dimension(486, 330));
		this.frame.getContentPane().setBackground(Color.BLACK);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setResizable(false);
		JLabel jl = new JLabel(new ImageIcon(this.drawBuffer));
		jl.addMouseListener(new KeyListener());
		jl.setBounds(0, 0, 470, 295);
		this.frame.setLayout(null);
		this.frame.add(jl);
		this.frame.setVisible(true);
		this.frame.setAlwaysOnTop (true);
		this.startThreads();
	}

	private void startThreads() {
		this.writeThread = new Thread(new WriteDaemon());
		this.writeThread.start();
		this.drawThread = new Timer(33, new DrawDaemon());
		this.drawThread.start();
	}

	@Override
	public void addKey(int keyId, StreamItem item) {
		if (keyId < this.keys.length && keyId >= 0) {
			this.keys[keyId] = item;
			this.updateQueue.add(new IconUpdate(keyId, item.getIcon()));
		}
		if(streamDeck != null)
			streamDeck.addKey(keyId, item);
	}

	@Override
	public boolean addKeyListener(StreamKeyListener listener) {
		boolean added = this.listerners.add(listener);
		return streamDeck != null ? streamDeck.addKeyListener(listener) : added;
	}

	@Override
	public boolean removeKeyListener(StreamKeyListener listener) {
		boolean removed = this.listerners.remove(listener);
		return streamDeck != null ? streamDeck.removeKeyListener(listener) : removed;
	}

	@Override
	public void drawImage(int keyId, SDImage imgData) {
		this.updateQueue.add(new IconUpdate(keyId, imgData));
		if(streamDeck != null)
			streamDeck.drawImage(keyId, imgData);
	}

	@Override
	public HidDevice getHidDevice() {
		return streamDeck != null ? this.streamDeck.getHidDevice() : null;
	}

	@Override
	public void removeKey(int keyId) {
		if (keyId < this.keys.length && keyId >= 0 && this.keys[keyId] != null) {
			this.keys[keyId] = null;
			this.updateQueue.add(new IconUpdate(keyId, StreamDeck.BLACK_ICON)); 
		}
		if(streamDeck != null)
			this.streamDeck.removeKey(keyId);
	}

	@Override
	public void reset() {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null)
				this.updateQueue.add(new IconUpdate(i, keys[i].getIcon()));
			else
				this.updateQueue.add(new IconUpdate(i, StreamDeck.BLACK_ICON));
		}
		if(streamDeck != null)
			this.streamDeck.reset();
	}

	@Override
	public void setBrightness(int brightness) {
		if(streamDeck != null)
			this.streamDeck.setBrightness(brightness);
	}

	@Override
	public void stop() {
		this.stopThrerads();
		if(streamDeck != null)
			this.streamDeck.stop();
	}

	private void stopThrerads() {
		this.run = false;
		this.drawThread.stop();
	}

	@Override
	public void waitForCompletion() {
		if(streamDeck != null)
			this.streamDeck.waitForCompletion();
	}

	@Override
	public void clearButton(int keyId) {
		this.updateQueue.add(new IconUpdate(keyId, StreamDeck.BLACK_ICON));
		if(streamDeck != null)
			this.streamDeck.clearButton(keyId);
	}

	@Override
	public boolean isHardware() {
		return this.streamDeck != null ? this.streamDeck.isHardware() : false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.frame.setTitle(name);
	}
	
	private class IconUpdate {

		public final int keyIndex;
		
		public final SDImage img;

		public IconUpdate(int keyIndex, SDImage img) {
			super();
			this.keyIndex = keyIndex;
			this.img = img;
		}
	}
	
	private class WriteDaemon implements Runnable  {
		@Override
		public void run() {
			while(SoftStreamDeck.this.run) {
				Graphics2D g = SoftStreamDeck.this.writeBuffer.createGraphics();
				while(!SoftStreamDeck.this.updateQueue.isEmpty()) {
					IconUpdate iu = SoftStreamDeck.this.updateQueue.poll();
					int spaceX = 20 + (90 * (4 - (iu.keyIndex % 5)));
					int spaceY = 20 + (90 * (iu.keyIndex / 5));
					g.drawImage(iu.img.image, spaceX, spaceY, null);
				}
				g.dispose();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class DrawDaemon implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SoftStreamDeck.this.drawGraphics.drawImage(SoftStreamDeck.this.writeBuffer, 0, 0, null);
			SoftStreamDeck.this.frame.repaint();
		}
	}
	
	private class KeyListener implements MouseListener {
		
		private int[] columnStart = {380, 290, 200, 110, 20};
		private int[] columnEnd = {450, 360, 270, 180, 90};
		
		private int[] rowStart = {20, 110, 200};
		private int[] rowEnd = {90, 180, 270};
		
		private int getIndex(int x, int y) {
			int column = -1;
			for (int i = 0; i < columnStart.length; i++) {
				if (x >= columnStart[i] && x < columnEnd[i]) {
					column = i;
					break;
				}
			}
			int row = -1;
			for (int i = 0; i < rowStart.length; i++) {
				if (y >= rowStart[i] && y < rowEnd[i]) {
					row = i;
					break;
				}
			}
			if(row >= 0 && column >= 0) {
				return (row*5) + column;
			}
			return -1;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int keyId = getIndex(e.getX(), e.getY());
			if (keyId >= 0) {
				for(StreamKeyListener listener : SoftStreamDeck.this.listerners) {
					KeyEvent evnt = new KeyEvent( SoftStreamDeck.this, keyId, Type.RELEASED_CLICKED);
					listener.onKeyEvent(evnt);
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int keyId = getIndex(e.getX(), e.getY());
			if (keyId >= 0) {
				for(StreamKeyListener listener : SoftStreamDeck.this.listerners) {
					KeyEvent evnt = new KeyEvent(SoftStreamDeck.this, keyId, Type.PRESSED);
					listener.onKeyEvent(evnt);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			this.mouseClicked(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}

}
