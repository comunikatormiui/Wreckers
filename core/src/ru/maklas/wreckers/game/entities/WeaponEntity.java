package ru.maklas.wreckers.game.entities;

import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.GameModel;

public abstract class WeaponEntity extends GameEntity {

    public WeaponEntity(int id, float x, float y, int zOrder, GameModel model) {
        super(id, EntityType.NEUTRAL_WEAPON, x, y, zOrder);
    }
}
