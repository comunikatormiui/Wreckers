package ru.maklas.wreckers.net_events;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class NetGrabZoneChange implements NetEvent {

	int entityId;
	boolean enable;

	public NetGrabZoneChange (int entityId, boolean enable) {
		this.entityId = entityId;
		this.enable = enable;
	}

	public NetGrabZoneChange () {

	}

	public NetGrabZoneChange setAndRet(int entityId, boolean enable) {
		this.entityId = entityId;
		this.enable = enable;
		return this;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public boolean getState() {
		return this.enable;
	}



	@Override
	public String toString() {
		return "NetGrabZoneChange{" +
		"entityId=" + entityId +
		", enable=" + enable +
		'}';
	}
}
