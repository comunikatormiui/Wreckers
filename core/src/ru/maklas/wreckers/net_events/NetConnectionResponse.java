package ru.maklas.wreckers.net_events;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 16.02.2018
 */
public class NetConnectionResponse implements NetEvent {

	boolean success;
	String error;

	public NetConnectionResponse(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	public NetConnectionResponse() {

	}

	public NetConnectionResponse setAndRet(boolean success, String error) {
		this.success = success;
		this.error = error;
		return this;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public String getError() {
		return this.error;
	}



	@Override
	public String toString() {
		return "NetConnectionResponse{" +
		"success=" + success +
		", error=" + error +
		'}';
	}
}
