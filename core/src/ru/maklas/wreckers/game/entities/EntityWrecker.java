package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.maklas.mengine.Engine;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.DamageEvent;
import ru.maklas.wreckers.engine.health.DeathEvent;
import ru.maklas.wreckers.engine.health.HealthComponent;
import ru.maklas.wreckers.engine.movemnet.AntiGravComponent;
import ru.maklas.wreckers.engine.movemnet.MotorComponent;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.status_effects.StatusEffectComponent;
import ru.maklas.wreckers.engine.weapon.GrabZoneComponent;
import ru.maklas.wreckers.engine.wrecker.WSocketComponent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.statics.Layers;

public class EntityWrecker extends GameEntity {

	private Body body;

	private static final int playerMass = 100;

	public EntityWrecker(int id, int type, float x, float y, float health) {
		super(id, type, x, y, Layers.playerZ);

		float bodyRadius = 40;
		float pickUpRadius = 75;
		float r = bodyRadius / Game.scale;
		float r2 = r * r;

		FixtureDef bodyF = A.physics.builders
				.newFixture()
				.shape(A.physics.builders
						.buildCircle(0, 0, bodyRadius))
				.friction(0.4f)
				.mask(type)
				.bounciness(0.2f)
				.density(playerMass / (MathUtils.PI * r2))
				.build();

		body = A.physics.builders.newBody(BodyDef.BodyType.DynamicBody)
				.pos(x, y)
				.linearDamp(1f)
				.addFixture(bodyF, new FixtureData(FixtureType.WRECKER_BODY))
				.angularDamp(5)
				.build();

		System.out.println(id + ": Player mass " + body.getMassData().mass);

		add(new PhysicsComponent(body));
		add(new HealthComponent(health));
		add(new MotorComponent(60));
		add(new AntiGravComponent(body.getMassData().mass,5, 5, 1.2f));
		add(new WSocketComponent(1, EntityType.weaponTypeFor(type)));
		add(new GrabZoneComponent(A.physics.builders.buildCircle(0, 0, pickUpRadius)));
		add(new WreckerComponent(
				0,
				0,
				0,
				0,
				10,
				20));
	}

	@Override
	protected void addedToEngine(final Engine engine) {
		add(new StatusEffectComponent(engine, this));
		subscribe(DeathEvent.class, e -> {
				if (e.getTarget() == EntityWrecker.this){
					engine.remove(EntityWrecker.this);
					System.out.println("Died");
				}
		});
	}
}
