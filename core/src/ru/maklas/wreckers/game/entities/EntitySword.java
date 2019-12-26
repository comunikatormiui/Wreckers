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
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.statics.Layers;

public class EntitySword extends WeaponEntity implements AttachAction {

	Body body;
	World world;
	PickUpComponent pickUpC;
	Joint lastJoint;

	public EntitySword(int id, float x, float y) {
		super(id, x, y, Layers.swordZ);
		this.world = A.physics.world;
		final float scale = 0.15f;
		final EntityType eType = EntityType.of(EntityType.NEUTRAL_WEAPON);

		PolygonShape handleShape = new PolygonShape();
		PolygonShape bladeShape = new PolygonShape();

		Vector2[] pointsHandle = new Vector2[]{
				new Vector2(400, 350),
				new Vector2(516, 235),
				new Vector2(516, 354 - 235),
				new Vector2(400, 354 - 350),
		};

		Vector2[] pointsSharp = new Vector2[]{
				new Vector2(516, 235),
				new Vector2(1377, 217),
				new Vector2(1500, 178),
				new Vector2(1377, 354 - 217),
				new Vector2(516, 354 - 235)
		};





		//Выравниваем центр масс в Origin
		for (Vector2 point : pointsHandle) {
			point.sub(832.3517f, 177.01413f);
		}

		for (Vector2 point : pointsSharp) {
			point.sub(832.3517f, 177.01413f);
		}

		//Скалируем
		for (Vector2 point : pointsHandle) {
			point.scl(scale / (Game.scale));
		}

		for (Vector2 point : pointsSharp) {
			point.scl(scale / (Game.scale));
		}

		handleShape.set(pointsHandle);
		bladeShape.set(pointsSharp);

		FixtureDef handle = A.physics.builders.newFixture()
				.bounciness(0.3f)
				.mask(eType)
				.friction(0.2f)
				.shape(handleShape)
				.density(9.187957f) //0.3768188
				.build();

		FixtureDef blade = A.physics.builders.newFixture()
				.mask(eType)
				.shape(bladeShape)
				.friction(0.2f)
				.bounciness(0.3f)
				.density(9.187957f) //1.2557532
				.build();


		body = A.physics.builders.newBody(BodyDef.BodyType.DynamicBody)
				.pos(x, y)
				.linearDamp(0.1f)
				.addFixture(handle, new FixtureData(FixtureType.WEAPON_NO_DAMAGE))
				.addFixture(blade, new WeaponPiercingFD(1, 0, 1490 * scale / Game.scale, 178 * scale / Game.scale))
				.angularDamp(0.1f)
				.build();

		System.out.println(id + ": Sword mass " + body.getMass());

		RenderUnit unit = new TextureUnit(A.images.sword);
		unit.scaleX = unit.scaleY = scale;
		unit.pivotX = unit.pivotY = 0;
		unit.localX = - 832.3517f * scale;
		unit.localY = - 177.01413f * scale;


		add(new PhysicsComponent(body));
		add(new RenderComponent(unit));
		pickUpC = new PickUpComponent(A.physics.builders.buildCircle((400 - 832.3517f) * scale, (178 - 177.01413f) * scale, 35), this);
		add(pickUpC);
		add(new WeaponComponent(
				35,
				35,
				35,
				1,
				1,
				1,
				25,
				25,
				10));
	}

	@Override
	public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.bodyA = ownerBody;
		rjd.bodyB = this.body;
		rjd.localAnchorA.set(socket.localX, socket.localY);
		rjd.localAnchorB.set(300, 178).sub(832.3517f, 177.01413f).scl(0.15f / Game.scale);
		return rjd;
	}
}
