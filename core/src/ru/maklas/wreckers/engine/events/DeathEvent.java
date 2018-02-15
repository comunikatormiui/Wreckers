package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class DeathEvent {

    private final Entity damageDealer;
    private final Entity target;

    public DeathEvent(Entity damageDealer, Entity target) {

        this.damageDealer = damageDealer;
        this.target = target;
    }

    public Entity getDamageDealer() {
        return damageDealer;
    }

    public Entity getTarget() {
        return target;
    }
}
