package ru.maklas.wreckers.game.entities;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.other.TTLComponent;
import ru.maklas.wreckers.engine.rendering.RenderComponent;

public class TestPointEntity extends Entity {

	public TestPointEntity(float x, float y, float ttl) {
		this(x, y, 1000000, ttl);
	}

	public TestPointEntity(float x, float y, int layer, float ttl) {
		super(x, y, layer);
		add(new RenderComponent(A.images.point));
		add(new TTLComponent(ttl));
	}
}
