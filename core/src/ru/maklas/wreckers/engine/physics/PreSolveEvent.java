package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import ru.maklas.wreckers.engine.other.Event;

public class PreSolveEvent implements Event {

	Contact contact;
	Manifold oldManifold;

	public PreSolveEvent(Contact contact, Manifold oldManifold) {

		this.contact = contact;
		this.oldManifold = oldManifold;
	}

	public Contact getContact() {
		return contact;
	}

	public Manifold getOldManifold() {
		return oldManifold;
	}
}
