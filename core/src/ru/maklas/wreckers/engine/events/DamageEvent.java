package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class DamageEvent {

    Entity target;
    float damage;
    Event hitEvent;

    public DamageEvent(Entity target, float damage, Event hitEvent) {
        this.target = target;
        this.damage = damage;
        this.hitEvent = hitEvent;
    }

    public Entity getTarget() {
        return target;
    }

    public float getDamage() {
        return damage;
    }

    public Event getHitEvent() {
        return hitEvent;
    }
}
