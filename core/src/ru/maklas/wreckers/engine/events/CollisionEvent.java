package ru.maklas.wreckers.engine.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import ru.maklas.mengine.Entity;

public class CollisionEvent {

    Entity a;
    Entity b;
    Contact contact;
    ContactImpulse impulse;
    boolean begin;

    public CollisionEvent(Entity a, Entity b, Contact contact, ContactImpulse impulse, boolean begin) {
        this.a = a;
        this.b = b;
        this.contact = contact;
        this.impulse = impulse;
        this.begin = begin;
    }

    public Entity getA() {
        return a;
    }

    public Entity getB() {
        return b;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean collisionBegin() {
        return begin;
    }

    public ContactImpulse getImpulse() {
        return impulse;
    }
}
