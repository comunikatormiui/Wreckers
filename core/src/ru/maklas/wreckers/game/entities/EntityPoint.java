package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.UpdatableEntity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.other.TTLComponent;
import ru.maklas.wreckers.engine.rendering.RenderComponent;
import ru.maklas.wreckers.engine.rendering.TextureUnit;

public class EntityPoint extends UpdatableEntity {

	private final float ttl;
	private Color color;

	public EntityPoint(Vector2 pos, Color c, float ttl) {
		this(pos.x, pos.y, c, ttl);
	}

	public EntityPoint(float x, float y, Color c, float ttl) {
		this.ttl = ttl;
		this.x = x;
		this.y = y;
		color = c;
	}

	@Override
	public void update(float dt) {

	}

	@Override
	protected void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		add(new RenderComponent(new TextureUnit(A.images.point).scale(0.2f)).color(color));
		add(new TTLComponent(ttl));
	}
}
