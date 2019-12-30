package ru.maklas.wreckers.game.entities;

import ru.maklas.wreckers.statics.EntityType;

public abstract class WeaponEntity extends GameEntity {

	public WeaponEntity(int id, float x, float y, int layer) {
		super(id, EntityType.NEUTRAL_WEAPON, x, y, layer);
	}
}
