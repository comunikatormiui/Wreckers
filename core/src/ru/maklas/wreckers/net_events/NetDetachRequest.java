package ru.maklas.wreckers.net_events;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 16.03.2018
 */
public class NetDetachRequest implements NetEvent {

	int playerId;
	int weaponId;

	public NetDetachRequest (int playerId, int weaponId) {
		this.playerId = playerId;
		this.weaponId = weaponId;
	}

	public NetDetachRequest () {

	}

	public NetDetachRequest setAndRet(int playerId, int weaponId) {
		this.playerId = playerId;
		this.weaponId = weaponId;
		return this;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public int getWeaponId() {
		return this.weaponId;
	}



	@Override
	public String toString() {
		return "NetDetachRequest{" +
		"playerId=" + playerId +
		", weaponId=" + weaponId +
		'}';
	}
}
