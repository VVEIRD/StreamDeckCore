package de.rcblum.stream.deck;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ExecutableButton implements StreamItem {
	
	int id = -1;
	
	BufferedImage img = null;

	String pathToExecutable = null;

	public ExecutableButton(int keyIndex, BufferedImage img, String pathToExecutable) {
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

}
