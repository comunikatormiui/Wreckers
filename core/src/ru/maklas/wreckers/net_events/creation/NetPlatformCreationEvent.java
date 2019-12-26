package ru.maklas.wreckers.net_events.creation;

import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

public class NetPlatformCreationEvent implements NetEvent {

	int id;
	float x;
	float y;
	float width;
	float height;

	public NetPlatformCreationEvent(int id, float x, float y, float width, float height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public NetPlatformCreationEvent() {

	}

	public NetPlatformCreationEvent setAndRet(int id, float x, float y, float width, float height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}


	@Override
	public String toString() {
		return "NetPlatformCreationEvent{" +
				"id=" + id +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				'}';
	}

}
