package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;

public class EntityPlatform extends GameEntity {


	public EntityPlatform(int id, float x, float y, int layer, float width, float height) {
		super(id, EntityType.OBSTACLE, x, y, layer);

		Body platformBody = A.physics.builders
				.newBody(BodyDef.BodyType.StaticBody)
				.addFixture(A.physics.builders.newFixture()
						.shape(A.physics.builders.buildRectangle(0, 0, width, height))
						.friction(0.7f)
						.density(10)
						.bounciness(0.2f)
						.mask(EntityType.OBSTACLE)
						.build(), new FixtureData(FixtureType.OBSTACLE))
				.pos(-360, 200)
				.linearDamp(0)
				.build();

		add(new PhysicsComponent(platformBody));
	}
}
