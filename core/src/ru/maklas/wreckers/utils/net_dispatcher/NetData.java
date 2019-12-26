package ru.maklas.wreckers.utils.net_dispatcher;

import com.badlogic.gdx.utils.Pool;

public class NetData implements Pool.Poolable{

	private long timeReceived;
	private NetEvent data;

	public NetData() {

	}

	public NetData(long timeReceived, NetEvent data) {
		this.timeReceived = timeReceived;
		this.data = data;
	}

	public long getTimeReceived() {
		return timeReceived;
	}

	public Object getData() {
		return data;
	}

	public NetData init(long timeReceived, NetEvent data) {
		this.timeReceived = timeReceived;
		this.data = data;
		return this;
	}

	public boolean isWithinLastSeconds(float seconds){
		long minTime = System.currentTimeMillis() - ((long) (seconds * 1000));
		return timeReceived > minTime;
	}

	@Override
	public void reset() {
		data = null;
	}
}
