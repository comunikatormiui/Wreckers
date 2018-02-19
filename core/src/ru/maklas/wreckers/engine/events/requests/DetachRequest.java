package ru.maklas.wreckers.engine.events.requests;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DetachRequest {


    public enum Type {
        /**
         * Удаляем самое первое оружие если оно имеется
         */
        FIRST,
        /**
         * Удаляем таргетное оружие. В таком случае weapon != null
         */
        TARGET}

    Entity wielder;
    @Nullable Entity weapon;
    Type type;

    public DetachRequest(Entity wielder, Type type, @Nullable Entity weapon) {
        this.wielder = wielder;
        this.weapon = weapon;
        this.type = type;
    }

    public Entity getWielder() {
        return wielder;
    }

    @Nullable
    public Entity getWeapon() {
        return weapon;
    }

    public Type getType() {
        return type;
    }
}
