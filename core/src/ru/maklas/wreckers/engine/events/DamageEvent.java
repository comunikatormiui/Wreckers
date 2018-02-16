package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DamageEvent {



    private final DamageSource source;
    private final Entity instigator;
    private final Entity target;
    private float damage;


    public DamageEvent(@Nullable Entity instigator, DamageSource damageSource, Entity target, float damage) {
        this.instigator = instigator;
        this.source = damageSource;
        this.target = target;
        this.damage = damage;
    }

    @Nullable
    public Entity getInstigator() {
        return instigator;
    }

    public float getDamage() {
        return damage;
    }

    public Entity getTarget() {
        return target;
    }

    public DamageSource getDamageSource() {
        return source;
    }
}
