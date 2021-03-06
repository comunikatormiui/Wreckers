package ru.maklas.wreckers.net_events.creation;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class NetScytheCreationEvent extends NetWeaponCreationEvent implements NetEvent {

	public NetScytheCreationEvent(int id, float x, float y, float angle) {
		super(id, x, y, angle);
	}

	public NetScytheCreationEvent() {

	}

	public NetScytheCreationEvent setAndRet() {
		return this;
	}



	@Override
	public String toString() {
		return "NetScytheCreationEvent{" +
				"id=" + id +
				", x=" + x +
				", y=" + y +
				", angle=" + angle +
				'}';
	}
}
