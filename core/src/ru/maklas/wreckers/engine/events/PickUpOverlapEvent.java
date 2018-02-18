package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class PickUpOverlapEvent {

    private Entity wielder;
    private Entity weapon;

    public PickUpOverlapEvent(Entity wielder, Entity weapon) {
        this.wielder = wielder;
        this.weapon = weapon;
    }

    public Entity getWielder() {
        return wielder;
    }

    public Entity getWeapon() {
        return weapon;
    }
}
