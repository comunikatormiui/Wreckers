package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Entity;

/** Request to attach weapon to the target **/
public class AttachRequest {

	private Entity wielder;
	private GrabZoneComponent playerPickUp;
	private Entity weapon;
	private PickUpComponent weaponPickUp;

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
