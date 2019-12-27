package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Request;

/** Request to attach weapon to the target **/
public class AttachRequest implements Request {

	private Entity wielder;
	private Entity weapon;

	public AttachRequest(Entity wielder, Entity weapon) {
		this.wielder = wielder;
		this.weapon = weapon;
	}

	public Entity getWielder() {
		return wielder;
	}

	public Entity getWeapon() {
		return weapon;
	}

}
