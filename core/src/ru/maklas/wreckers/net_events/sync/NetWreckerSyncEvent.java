package ru.maklas.wreckers.net_events.sync;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class NetWreckerSyncEvent implements NetEvent {

	NetBodySyncEvent pos;
	float motorX;
	float motorY;

	public NetWreckerSyncEvent(NetBodySyncEvent pos, float motorX, float motorY) {
		this.pos = pos;
		this.motorX = motorX;
		this.motorY = motorY;
	}

	public NetWreckerSyncEvent() {

	}

	public NetWreckerSyncEvent setAndRet(NetBodySyncEvent pos, float motorX, float motorY) {
		this.pos = pos;
		this.motorX = motorX;
		this.motorY = motorY;
		return this;
	}

	public NetBodySyncEvent getPos() {
		return this.pos;
	}

	public float getMotorX() {
		return this.motorX;
	}

	public float getMotorY() {
		return this.motorY;
	}

	public int getId(){
		return pos.getId();
	}



	@Override
	public String toString() {
		return "NetWreckerSyncEvent{" +
		"pos=" + pos +
		", motorX=" + motorX +
		", motorY=" + motorY +
		'}';
	}
}
