package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.damage.DamageData;

public class DamageEvent {


    DamageData data;

    public DamageEvent(DamageData data) {
        this.data = data;
    }


    public DamageData getData() {
        return data;
    }

    public DamageType getType() {
        return data.getType();
    }

    @NotNull
    public Entity getTarget() {
        return data.getTarget();
    }

    @Nullable
    public Entity getDamageDealer() {
        return data.getDamageDealer();
    }

    public float getDamage() {
        return data.getDamage();
    }

}
