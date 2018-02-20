package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DamageEvent {

    DamageType type;
    Entity target;
    @Nullable
    Entity damageDealer;
    @Nullable Entity weapon;
    float damage;

    public DamageEvent(DamageType type, float damage, Entity target, @Nullable Entity damageDealer, @Nullable Entity weapon) {
        this.type = type;
        this.damage = damage;
        this.target = target;
        this.damageDealer = damageDealer;
        this.weapon = weapon;
    }

    public DamageType getType() {
        return type;
    }

    public Entity getTarget() {
        return target;
    }

    @Nullable
    public Entity getDamageDealer() {
        return damageDealer;
    }

    @Nullable
    public Entity getWeapon() {
        return weapon;
    }

    public float getDamage() {
        return damage;
    }
}
