package ru.maklas.wreckers.net_events;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 16.02.2018
 */
public class NetConnectionRequest implements NetEvent {

	String name;
	String version;

	public NetConnectionRequest(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public NetConnectionRequest() {

	}

	public NetConnectionRequest setAndRet(String name, String version) {
		this.name = name;
		this.version = version;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}



	@Override
	public String toString() {
		return "NetConnectionRequest{" +
		"name=" + name +
		", version=" + version +
		'}';
	}
}
