package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import ru.maklas.wreckers.engine.other.Event;

public class PostSolveEvent implements Event {

	private final Contact contact;
	private final ContactImpulse impulse;

	public PostSolveEvent(Contact contact, ContactImpulse impulse) {

		this.contact = contact;
		this.impulse = impulse;
	}

	public Contact getContact() {
		return contact;
	}

	public ContactImpulse getImpulse() {
		return impulse;
	}
}
