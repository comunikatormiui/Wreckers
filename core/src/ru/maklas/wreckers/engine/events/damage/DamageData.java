package ru.maklas.wreckers.engine.events.damage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.DamageType;

public abstract class DamageData {


    DamageType type;
    Entity target;
    @Nullable Entity damageDealer;
    float damage;

    public DamageData(DamageType type, @NotNull Entity target, @Nullable Entity damageDealer, float damage) {
        this.type = type;
        this.target = target;
        this.damageDealer = damageDealer;
        this.damage = damage;
    }

    public DamageType getType() {
        return type;
    }

    @NotNull
    public Entity getTarget() {
        return target;
    }

    @Nullable
    public Entity getDamageDealer() {
        return damageDealer;
    }

    public float getDamage() {
        return damage;
    }
}
