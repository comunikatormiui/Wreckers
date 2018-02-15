package ru.maklas.wreckers.engine.events;

import com.badlogic.gdx.physics.box2d.Contact;
import ru.maklas.mengine.Entity;

public class CollisionEvent {

    Entity a;
    Entity b;
    Contact contact;
    boolean begin;

    public CollisionEvent(Entity a, Entity b, Contact contact, boolean begin) {
        this.a = a;
        this.b = b;
        this.contact = contact;
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
}
