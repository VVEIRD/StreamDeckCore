package de.rcblum.stream.deck.device;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rcblum.stream.deck.event.KeyEvent;
import de.rcblum.stream.deck.event.StreamKeyListener;
import de.rcblum.stream.deck.event.KeyEvent.Type;
import de.rcblum.stream.deck.items.StreamItem;
import de.rcblum.stream.deck.util.IconHelper;
import de.rcblum.stream.deck.util.SDImage;
import purejavahidapi.HidDevice;

public class SoftStreamDeck implements IStreamDeck {
	
	private static List<SoftStreamDeck> instances = new ArrayList<>(5);
	
	public static void showDecks() {
		for (SoftStreamDeck softStreamDeck : instances) {
			softStreamDeck.frame.setVisible(true);
		}
	}
	
	public static void hideDecks() {
		for (SoftStreamDeck softStreamDeck : instances) {
			softStreamDeck.frame.setVisible(false);
		}
	}
	
	private static Logger logger = LogManager.getLogger(SoftStreamDeck.class);

	private String name = null;
		
	private IStreamDeck streamDeck = null;
	
	StreamItem[] keys = null;
	
	private List<StreamKeyListener> listerners;
	
	private JFrame frame = null;

	private BufferedImage writeBuffer = null;
	
	private BufferedImage drawBuffer = null;
	
	private Graphics2D drawGraphics = null;

	private Thread writeThread = null;
	
	private Timer drawThread = null;
	
	private boolean running = true;

	/**
	 * Daemon that sends received {@link KeyEvent}s  to the affected listeners.
	 */
	private Thread eventDispatcher = null;

	/**
	 * Queue for {@link KeyEvent}s that are triggered by the ESD
	 */
	Queue<KeyEvent> recievePool = new ConcurrentLinkedQueue<>();
	
	private ConcurrentLinkedQueue<IconUpdate> updateQueue = new ConcurrentLinkedQueue<>();

	
	public SoftStreamDeck(String name, IStreamDeck streamDeck) {
		this.streamDeck = streamDeck;
		this.keys = new StreamItem[streamDeck != null ? this.streamDeck.getKeySize() : 15];
		listerners = new ArrayList<>(4);
		this.writeBuffer = IconHelper.getImageFromResource("/resources/sd-background.png");
		this.drawBuffer = IconHelper.getImageFromResource("/resources/sd-background.png");
		this.name = name;
		this.drawGraphics = this.drawBuffer.createGraphics();
		this.frame = new JFrame(name) {
			private static final long serialVersionUID = 1L;
			@Override
			public void dispose() {
				SoftStreamDeck.this.stopThreads();
				super.dispose();
			}
		};
		this.frame.setSize(new Dimension(486, 330));
		this.frame.getContentPane().setBackground(Color.BLACK);
		this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.frame.setResizable(false);
		JLabel jl = new JLabel(new ImageIcon(this.drawBuffer));
		jl.addMouseListener(new KeyListener());
		jl.setBounds(0, 0, 470, 295);
		DragListener dl = new DragListener();
		jl.addMouseListener(dl);
		jl.addMouseMotionListener(dl);
		this.frame.setLayout(null);
		this.frame.add(jl);
		this.frame.setVisible(true);
		this.frame.setAlwaysOnTop (true);
		this.startThreads();
		instances.add(this);
	}
	
	@Override
	public int getKeySize() {
		return this.keys.length;
	}

	private void startThreads() {
		this.writeThread = new Thread(new WriteDaemon());
		this.writeThread.start();
		this.drawThread = new Timer(33, new DrawDaemon());
		this.drawThread.start();
		this.eventDispatcher = new Thread(new EventDispatcher());
		this.eventDispatcher.setDaemon(true);
		this.eventDispatcher.start();
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
		if(streamDeck != null)
			this.streamDeck.stop();
		this.stopThreads();
		this.frame.setVisible(false);
		this.frame.dispose();
	}

	private void stopThreads() {
		this.running = false;
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
			while(SoftStreamDeck.this.running) {
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
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int keyId = getIndex(e.getX(), e.getY());
			if (keyId >= 0) {
				KeyEvent evnt = new KeyEvent(SoftStreamDeck.this, keyId, Type.PRESSED);
				SoftStreamDeck.this.recievePool.add(evnt);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int keyId = getIndex(e.getX(), e.getY()); 
			if (keyId >= 0) {
				KeyEvent evnt = new KeyEvent( SoftStreamDeck.this, keyId, Type.RELEASED_CLICKED);
				SoftStreamDeck.this.recievePool.add(evnt);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}

	/**
	 * Dispatcher that asynchronously sends out all issued {@link KeyEvent}s.
	 * @author Roland von Werden
	 *
	 */
	private class EventDispatcher implements Runnable {

		@Override
		public void run() {
			while (SoftStreamDeck.this.running || !SoftStreamDeck.this.running && !recievePool.isEmpty()) {
				if (!SoftStreamDeck.this.recievePool.isEmpty()) {
					KeyEvent event = SoftStreamDeck.this.recievePool.poll();
					int i = event.getKeyId();
					if (i < SoftStreamDeck.this.keys.length && SoftStreamDeck.this.keys[i] != null) {
						SoftStreamDeck.this.keys[i].onKeyEvent(event);
					}
					SoftStreamDeck.this.listerners.stream().forEach(l -> 
						{
							try {
								l.onKeyEvent(event);
							} 
							catch (Exception e) {
								logger.error("Error sending out KeyEvents");
								logger.error(e);
								e.printStackTrace();
							}
						}
					);
				}
				if (SoftStreamDeck.this.recievePool.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						logger.error("EventDispatcher sleep interrupted");
						logger.error(e);
					}
				}
			}
		}

	}
	
	public class DragListener extends MouseInputAdapter
	{
	    Point location;
	    MouseEvent pressed;
	 
	    public void mousePressed(MouseEvent me)
	    {
	        pressed = me;
	    }
	 
	    public void mouseDragged(MouseEvent me)
	    {
	        Component component = frame;
	        location = component.getLocation(location);
	        int x = location.x - pressed.getX() + me.getX();
	        int y = location.y - pressed.getY() + me.getY();
	        frame.setLocation(x, y);
	     }
	}

}
