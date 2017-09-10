package de.rcblum.stream.deck.event;

/**
 * Listener for receiving events when interactions with the stream deck happens
 * 
 * @author rcBlum
 *
 */
public interface StreamKeyListener {

	/**
	 * Whenever a key event is triggered this method will be called
	 * @param event
	 */
	public void onKeyEvent(KeyEvent event);

}
