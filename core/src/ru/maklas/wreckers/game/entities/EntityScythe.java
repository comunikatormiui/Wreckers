package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.maklas.bodymaker.runtime.save_beans.BodyPoly;
import ru.maklas.bodymaker.runtime.save_beans.NamedPoint;
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

public class EntityScythe extends WeaponEntity implements AttachAction {


	Body body;
	World world;
	PickUpComponent pickUpC;
	final float scale = 0.65f;
	float handleX;
	float handleY;

	public EntityScythe(int id, float x, float y, int zOrder) {
		super(id, x, y, zOrder);
		this.world = A.physics.world;
		final EntityType eType = EntityType.of(EntityType.NEUTRAL_WEAPON);

		BodyPoly bodyPoly = BodyPoly.fromJson(Gdx.files.internal("scythe.json").readString());

		NamedPoint mass_center = bodyPoly.getMassCenter();
		NamedPoint centerWannaBe = bodyPoly.findPoint("centerWannaBe");
		NamedPoint peak = bodyPoly.findPoint("peak");
		NamedPoint origin = bodyPoly.findPoint("Origin");

		RenderUnit unit = new TextureUnit(A.images.scythe);
		unit.scaleX = unit.scaleY = scale;
		unit.pivotX = mass_center.x / unit.width;
		unit.pivotY = mass_center.y / unit.height;

		bodyPoly
				.mov(-mass_center.x, -mass_center.y) // теперь координаты (0, 0) совпадают с координатами центра массы
				.scale(scale/Game.scale); // Подгоняем размер.



		PolygonShape handle	 = bodyPoly.findShape("handle").toPolygonShape();
		PolygonShape hammerSide = bodyPoly.findShape("hammerSide").toPolygonShape();
		PolygonShape blade	  = bodyPoly.findShape("blade").toPolygonShape();
		PolygonShape blade2	 = bodyPoly.findShape("blade2").toPolygonShape();
		PolygonShape edge	   = bodyPoly.findShape("edge").toPolygonShape();


		FixtureDef fix = A.physics.builders.newFixture()
				.bounciness(0.1f)
				.mask(eType)
				.friction(1f)
				.shape(handle)
				.density(1)
				.build();

		FixtureDef fix2 = A.physics.builders.newFixture()
				.mask(eType)
				.shape(hammerSide)
				.friction(0.2f)
				.bounciness(0.1f)
				.density(1)
				.build();

		FixtureDef fix3 = A.physics.builders.newFixture()
				.mask(eType)
				.shape(blade)
				.friction(0.2f)
				.bounciness(0.1f)
				.density(1)
				.build();

		FixtureDef fix4 = A.physics.builders.newFixture()
				.mask(eType)
				.shape(blade2)
				.friction(0.2f)
				.bounciness(0.1f)
				.density(1)
				.build();

		FixtureDef fix5 = A.physics.builders.newFixture()
				.mask(eType)
				.shape(edge)
				.friction(0.2f)
				.bounciness(0.1f)
				.density(1)
				.build();

		body = A.physics.builders.newBody(BodyDef.BodyType.DynamicBody)
				.pos(x, y)
				.linearDamp(0.1f)
				.addFixture(fix, new FixtureData(FixtureType.WEAPON_NO_DAMAGE))
				.addFixture(fix2, new FixtureData(FixtureType.WEAPON_DAMAGE))
				.addFixture(fix3, new FixtureData(FixtureType.WEAPON_DAMAGE))
				.addFixture(fix4, new FixtureData(FixtureType.WEAPON_DAMAGE))
				.addFixture(fix5, new WeaponPiercingFD(0.5f, -2f, 217 * scale / Game.scale, 60 * scale / Game.scale))
				.angularDamp(0.1f)
				.build();

		add(new PhysicsComponent(body));
		add(new RenderComponent(unit));
		handleX = origin.x * Game.scale + 30 * scale;
		handleY = origin.y * Game.scale + 30 * scale;
		pickUpC = new PickUpComponent(A.physics.builders.buildCircle(handleX, handleY, 35), this);
		add(pickUpC);
		add(new WeaponComponent(
				10,
				50,
				50,
				1,
				1,
				1,
				12,
				55,
				25));
	}

	@Override
	public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.bodyA = ownerBody;
		rjd.bodyB = this.body;
		rjd.localAnchorA.set(socket.localX, socket.localY);
		rjd.localAnchorB.set(handleX, handleY).scl(1 / Game.scale);
		return rjd;
	}
}
