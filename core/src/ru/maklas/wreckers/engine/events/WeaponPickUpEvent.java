package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class WeaponPickUpEvent {

    private Entity wielder;
    private Entity weapon;
    private boolean pickedUp;

    public WeaponPickUpEvent(Entity wielder, Entity weapon, boolean pickedUp) {
        this.wielder = wielder;
        this.weapon = weapon;
        this.pickedUp = pickedUp;
    }

    public Entity getWielder() {
        return wielder;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }
}
