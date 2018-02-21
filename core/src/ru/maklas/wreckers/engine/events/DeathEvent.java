package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class DeathEvent {

    Entity target;
    private final Event lastHitEvent;

    public DeathEvent(Entity target, Event lastHitEvent) {
        this.target = target;
        this.lastHitEvent = lastHitEvent;
    }

    public Entity getTarget() {
        return target;
    }

    public Event getLastHitEvent() {
        return lastHitEvent;
    }
}
