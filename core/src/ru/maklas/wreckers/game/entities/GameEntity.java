package ru.maklas.wreckers.game.entities;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;

public class GameEntity extends Entity{


    public GameEntity(int id, EntityType eType, float x, float y, int zOrder) {
        super(id, x, y, zOrder);
        this.type = eType.type;
    }
}
