package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Event;
import ru.maklas.wreckers.statics.Game;

/** Post process data. Can be triggered much more often **/
public class PostCollisionEvent implements Event {

	private Entity a;
	private Entity b;
	private Fixture fixA;
	private Fixture fixB;
	private final Vector2 point = new Vector2();
	private final Vector2 normal = new Vector2();
	private float normalImpulse;
	private float tangentImpulse;

	public PostCollisionEvent() {
	}

	public PostCollisionEvent init(Entity a, Entity b, Fixture fixA, Fixture fixB, Vector2 point, Vector2 normal, float normalImpulse, float tangentImpulse) {
		this.a = a;
		this.b = b;
		this.fixA = fixA;
		this.fixB = fixB;
		this.point.set(point);
		this.normal.set(normal);
		this.normalImpulse = normalImpulse;
		this.tangentImpulse = tangentImpulse;
		return this;
	}

	public PostCollisionEvent init(Contact contact, ContactImpulse impulse) {
		fixA = contact.getFixtureA();
		fixB = contact.getFixtureB();
		a = ((Entity) fixA.getBody().getUserData());
		b = ((Entity) fixB.getBody().getUserData());

		WorldManifold wm = contact.getWorldManifold();
		point.set(wm.getPoints()[0]).scl(Game.scale);
		normal.set(wm.getNormal());
		normalImpulse = impulse.getNormalImpulses()[0];
		tangentImpulse = impulse.getTangentImpulses()[0];
		return this;
	}

	public Entity getA() {
		return a;
	}

	public Entity getB() {
		return b;
	}

	public Fixture getFixA() {
		return fixA;
	}

	public Fixture getFixB() {
		return fixB;
	}

	/** Point in Libgdx's coordinates. Not Box2d's**/
	public Vector2 getPoint() {
		return point;
	}

	public Vector2 getNormal() {
		return normal;
	}

	public float getNormalImpulse() {
		return normalImpulse;
	}

	public float getTangentImpulse() {
		return tangentImpulse;
	}


	/** Меняет A и B местами, с учётом вектора нормали и возвращает. <b>Внимание! Мутирует данный объект!</b> **/
	public PostCollisionEvent reverse(){
		return init(b, a, fixB, fixA, point, new Vector2(normal.x * -1, normal.y * -1), normalImpulse, tangentImpulse);
	}

}
