package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Event;

public class WeaponWreckerHitEvent implements Event {

	Entity weapon;
	@Nullable Entity weaponOwner;
	Entity targetWrecker;
	Vector2 point;
	Vector2 normal;
	Vector2 collisionVelocity;
	float impulse;
	float sliceness;
	float dullness;
	float sharpness;
	Fixture weaponFix;
	Fixture wreckerFix;

	public WeaponWreckerHitEvent() {

	}
	public WeaponWreckerHitEvent(Entity weapon, @Nullable Entity weaponOwner, Entity targetWrecker, Vector2 point, Vector2 normal, Vector2 collisionVelocity, float impulse, float dullness, float sliceness, float sharpness, Fixture weaponFix, Fixture wreckerFix) {
		this.weapon = weapon;
		this.weaponOwner = weaponOwner;
		this.targetWrecker = targetWrecker;
		this.point = point;
		this.collisionVelocity = collisionVelocity;
		this.impulse = impulse;
		this.normal = normal;
		this.sliceness = sliceness;
		this.dullness = dullness;
		this.sharpness = sharpness;
		this.weaponFix = weaponFix;
		this.wreckerFix = wreckerFix;
	}

	public Entity getWeapon() {
		return weapon;
	}

	@Nullable
	public Entity getWeaponOwner() {
		return weaponOwner;
	}

	public Entity getTargetWrecker() {
		return targetWrecker;
	}

	/** Engine scale **/
	public Vector2 getPoint() {
		return point;
	}

	public float getImpulse() {
		return impulse;
	}

	/** Vector pointing from body to the weapon **/
	public Vector2 getNormal() {
		return normal;
	}

	public Vector2 getCollisionVelocity() {
		return collisionVelocity;
	}

	public float getSliceness() {
		return sliceness;
	}

	public float getSharpness() {
		return sharpness;
	}

	public float getDullness() {
		return dullness;
	}

	public Fixture getWeaponFix() {
		return weaponFix;
	}

	public Fixture getWreckerFix() {
		return wreckerFix;
	}

}
