package ru.maklas.wreckers.net_events.state_change;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 16.03.2018
 */
public class NetRestartEvent implements NetEvent {


	public NetRestartEvent() {

	}

	public NetRestartEvent setAndRet() {
		return this;
	}

	@Override
	public String toString() {
		return "NetRetryEvent{" +
		'}';
	}
}
