package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Request;

public class GrabZoneChangeRequest implements Request {

	private boolean enable;
	private Entity entity;

	public GrabZoneChangeRequest(boolean enable, Entity entity) {
		this.enable = enable;
		this.entity = entity;
	}

	public boolean state() {
		return enable;
	}

	public Entity getEntity() {
		return entity;
	}
}
