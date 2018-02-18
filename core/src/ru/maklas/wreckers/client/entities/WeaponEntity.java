package ru.maklas.wreckers.client.entities;

import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.ClientGameModel;

public abstract class WeaponEntity extends GameEntity {

    public WeaponEntity(int id, EntityType eType, float x, float y, int zOrder, ClientGameModel model) {
        super(id, eType, x, y, zOrder);
    }
}
