package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.components.GrabZoneComponent;
import ru.maklas.wreckers.engine.components.PickUpComponent;

public class AttachRequest {

    Entity wielder;
    GrabZoneComponent playerPickUp;
    Entity weapon;
    PickUpComponent weaponPickUp;

    public AttachRequest(Entity wielder, GrabZoneComponent playerPickUp, Entity weapon, PickUpComponent weaponPickUp) {
        this.wielder = wielder;
        this.playerPickUp = playerPickUp;
        this.weapon = weapon;
        this.weaponPickUp = weaponPickUp;
    }

    public Entity getWielder() {
        return wielder;
    }

    public GrabZoneComponent getPlayerPickUp() {
        return playerPickUp;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public PickUpComponent getPickUp() {
        return weaponPickUp;
    }

}
