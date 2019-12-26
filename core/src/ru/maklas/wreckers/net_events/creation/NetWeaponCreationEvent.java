package ru.maklas.wreckers.net_events.creation;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public abstract class NetWeaponCreationEvent implements NetEvent {

	protected int id;
	protected float x;
	protected float y;
	protected float angle;

	public NetWeaponCreationEvent(int id, float x, float y, float angle) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public NetWeaponCreationEvent() {

	}

	public NetWeaponCreationEvent setAndRet(int id, float x, float y, float angle) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
		return this;
	}

	public int getId() {
		return this.id;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getAngle() {
		return this.angle;
	}



	@Override
	public String toString() {
		return "NetWeaponCreationEvent{" +
		"id=" + id +
		", x=" + x +
		", y=" + y +
		", angle=" + angle +
		'}';
	}
}
