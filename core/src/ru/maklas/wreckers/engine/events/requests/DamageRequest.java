package ru.maklas.wreckers.engine.events.requests;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.DamageType;

public class DamageRequest {

    DamageType type;
    Entity target;
    @Nullable Entity damageDealer;
    @Nullable Entity weapon;
    float damage;

    public DamageRequest(DamageType type, float damage, Entity target, @Nullable Entity damageDealer, @Nullable Entity weapon) {
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
