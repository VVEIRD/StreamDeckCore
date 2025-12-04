package de.rcblum.stream.deck.animation;

import de.rcblum.stream.deck.event.KeyEvent.Type;

public interface AnimationTrigger {

	default boolean isTriggered(Type keyEventType) {return false;}

}
