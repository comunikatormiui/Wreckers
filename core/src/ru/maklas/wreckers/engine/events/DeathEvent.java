package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DeathEvent {

    @Nullable
    private final Entity instigator;
    private final DamageSource bulletHit;
    private final Entity target;

    public DeathEvent(@Nullable Entity instigator, DamageSource bulletHit, Entity target) {

        this.instigator = instigator;
        this.bulletHit = bulletHit;
        this.target = target;
    }

    @Nullable
    public Entity getInstigator() {
        return instigator;
    }

    public DamageSource getBulletHit() {
        return bulletHit;
    }

    public Entity getTarget() {
        return target;
    }
}
