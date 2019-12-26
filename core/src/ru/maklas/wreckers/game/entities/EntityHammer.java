package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.rendering.RenderComponent;
import ru.maklas.wreckers.engine.rendering.RenderUnit;
import ru.maklas.wreckers.engine.rendering.TextureUnit;
import ru.maklas.wreckers.engine.weapon.AttachAction;
import ru.maklas.wreckers.engine.weapon.PickUpComponent;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.wrecker.WSocket;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.statics.Layers;

public class EntityHammer extends WeaponEntity implements AttachAction {

	private Body body;
	private PickUpComponent pickUpC;

	public EntityHammer(int id, float x, float y) {
		super(id, x, y, Layers.hammerZ);
		final float scale = 0.13f;
		final int type = EntityType.NEUTRAL_WEAPON;

		PolygonShape handle = new PolygonShape();
		PolygonShape sharp = new PolygonShape();

		Vector2[] pointsHandle = new Vector2[]{
				new Vector2(8, 245),
				new Vector2(767, 254),
				new Vector2(767, 575 - 254),
				new Vector2(8, 575 - 245)
		};

		Vector2[] pointsBody = new Vector2[]{
				new Vector2(762, 8),
				new Vector2(1045, 8),
				new Vector2(1045, 575 - 8),
				new Vector2(762, 575 - 8)
		};

		for (Vector2 point : pointsHandle) {
			point.scl(scale / (Game.scale));
		}

		for (Vector2 point : pointsBody) {
			point.scl(scale / (Game.scale));
		}

		handle.set(pointsHandle);
		sharp.set(pointsBody);

		FixtureDef fix = A.physics.builders.newFixture()
				.bounciness(0.1f)
				.mask(type)
				.friction(1f)
				.shape(handle)
				.density(8.770995f) //0.60928726
				.build();

		FixtureDef fix2 = A.physics.builders.newFixture()
				.mask(type)
				.shape(sharp)
				.friction(0.2f)
				.bounciness(0.1f)
				.density(8.770995f) //1.6709557
				.build();

		body = A.physics.builders.newBody(BodyDef.BodyType.DynamicBody)
				.pos(x, y)
				.linearDamp(0.1f)
				.addFixture(fix, new FixtureData(FixtureType.WEAPON_NO_DAMAGE))
				.addFixture(fix2, new FixtureData(FixtureType.WEAPON_DAMAGE))
				.angularDamp(0.1f)
				.build();

		System.out.println(id + ": Hammer mass " + body.getMass());

		RenderUnit unit = new TextureUnit(A.images.hammer);
		unit.scaleX = unit.scaleY = scale;
		unit.pivotX = unit.pivotY = 0;


		add(new PhysicsComponent(body));
		add(new RenderComponent(unit));
		pickUpC = new PickUpComponent(A.physics.builders.buildCircle(8 * scale, 287 * scale, 35), this);
		add(pickUpC);
		add(new WeaponComponent(
				50,
				0,
				0,
				1,
				1,
				1,
				100,
				55,
				40));
	}

	@Override
	public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.bodyA = ownerBody;
		rjd.bodyB = this.body;
		rjd.localAnchorA.set(socket.localX, socket.localY);
		rjd.localAnchorB.set(8, 287).scl(0.13f / Game.scale);
		return rjd;
	}
}
