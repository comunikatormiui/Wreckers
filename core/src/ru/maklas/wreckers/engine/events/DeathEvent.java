package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DeathEvent {

    Entity target;
    @Nullable Entity lastDamageDealer;
    DamageType lastDamageType;

    public DeathEvent(Entity target, DamageType lastDamageType, @Nullable Entity lastDamageDealer) {
        this.target = target;
        this.lastDamageType = lastDamageType;
        this.lastDamageDealer = lastDamageDealer;
    }


    public Entity getTarget() {
        return target;
    }

    @Nullable
    public Entity getLastDamageDealer() {
        return lastDamageDealer;
    }

    public DamageType getLastDamageType() {
        return lastDamageType;
    }
}
