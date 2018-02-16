package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.game.WeaponType;

public class WeaponChangeRequest {

    private Entity entity;
    private WeaponType weaponType;

    public WeaponChangeRequest(Entity entity, WeaponType weaponType) {
        this.entity = entity;
        this.weaponType = weaponType;
    }

    public Entity getEntity() {
        return entity;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }
}
