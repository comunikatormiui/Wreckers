package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class DamageEvent {

    private final Entity damageDealer;
    private final Entity target;
    private float damage;


    public DamageEvent(Entity damageDealer, Entity target, float damage) {
        this.damageDealer = damageDealer;
        this.target = target;
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public Entity getTarget() {
        return target;
    }

    public Entity getDamageDealer() {
        return damageDealer;
    }
}
