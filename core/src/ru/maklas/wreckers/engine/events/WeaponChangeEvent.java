package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.game.Weapon;

public class WeaponChangeEvent {

    Entity entity;
    Weapon oldWeapon;
    Weapon newWeapon;

    public WeaponChangeEvent(Entity entity, Weapon oldWeapon, Weapon newWeapon) {
        this.entity = entity;
        this.oldWeapon = oldWeapon;
        this.newWeapon = newWeapon;
    }
}
