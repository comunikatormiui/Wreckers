package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.components.PlayerPickUpComponent;
import ru.maklas.wreckers.engine.components.WeaponPickUpComponent;

public class WeaponPickUpRequest {

    Entity wielder;
    PlayerPickUpComponent playerPickUp;
    Entity weapon;
    WeaponPickUpComponent weaponPickUp;


    public WeaponPickUpRequest(Entity wielder, PlayerPickUpComponent playerPickUp, Entity weapon, WeaponPickUpComponent weaponPickUp) {
        this.wielder = wielder;
        this.playerPickUp = playerPickUp;
        this.weapon = weapon;
        this.weaponPickUp = weaponPickUp;
    }

    public Entity getWielder() {
        return wielder;
    }

    public PlayerPickUpComponent getPlayerPickUp() {
        return playerPickUp;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public WeaponPickUpComponent getWeaponPickUp() {
        return weaponPickUp;
    }
}
