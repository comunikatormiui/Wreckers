package ru.maklas.wreckers.net_events;


import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 16.03.2018
 */
public class NetGrabZoneChangeRequest implements NetEvent {

	int playerId;
	boolean enable;

	public NetGrabZoneChangeRequest (int playerId, boolean enable) {
		this.playerId = playerId;
		this.enable = enable;
	}

	public NetGrabZoneChangeRequest () {

	}

	public NetGrabZoneChangeRequest setAndRet(int playerId, boolean enable) {
		this.playerId = playerId;
		this.enable = enable;
		return this;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public boolean getEnable() {
		return this.enable;
	}



	@Override
	public String toString() {
		return "NetGrabZoneChangeRequest{" +
		"playerId=" + playerId +
		", enable=" + enable +
		'}';
	}
}
