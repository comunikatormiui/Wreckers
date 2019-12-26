package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Entity;

/** Внутридвижковый ивент. Запрос на детач оружия **/
public class DetachRequest {

	public enum Type {
		/** Удаляем самое первое оружие если оно имеется. wielder != null **/
		FIRST,
		/** Удаляем таргетное оружие из таргетного Entity. В таком случае weapon != null и wielder != null **/
		TARGET_ENTITY_AND_WEAPON,
		/** Отсоеденияем указанное Оружие. wielder может быть null **/
		TARGET_WEAPON

	}

	private Entity wielder;
	private Entity weapon;
	private Type type;

	public DetachRequest(Type type, Entity wielder, Entity weapon) {
		this.wielder = wielder;
		this.weapon = weapon;
		this.type = type;
	}

	public Entity getWielder() {
		return wielder;
	}

	public Entity getWeapon() {
		return weapon;
	}

	public Type getType() {
		return type;
	}
}
