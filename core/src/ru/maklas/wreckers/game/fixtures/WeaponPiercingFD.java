package ru.maklas.wreckers.game.fixtures;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public class WeaponPiercingFD extends WeaponDamageFixtureData {

	private final Vector2 pierceDirection = new Vector2();
	private Vector2 stuckPoint = new Vector2();

	public WeaponPiercingFD(Vector2 pierceDirection, Vector2 stuckPoint) {
		this.pierceDirection.set(pierceDirection).nor();
		this.stuckPoint.set(stuckPoint);
	}

	public WeaponPiercingFD(float dirX, float dirY, float stuckX, float stuckY) {
		this.pierceDirection.set(dirX, dirY).nor();
		this.stuckPoint.set(stuckX, stuckY);
	}

	public Vector2 getPierceDirection() {
		return pierceDirection;
	}

	public void getWorldDirection(Fixture fixture, Vector2 v){
		v.set(pierceDirection).rotate(fixture.getBody().getAngle() * MathUtils.radiansToDegrees);
	}

	public Vector2 getStuckPoint() {
		return stuckPoint;
	}
}
