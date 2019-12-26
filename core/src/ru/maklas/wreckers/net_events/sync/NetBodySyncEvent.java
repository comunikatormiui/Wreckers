package ru.maklas.wreckers.net_events.sync;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.jetbrains.annotations.NotNull;
import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 14.03.2018
 * Синхронизует тела. Используется Box2d скейлинг. Угол в радианах
 */
public class NetBodySyncEvent implements NetEvent {

	int entityId;
	float x;
	float y;
	float velX;
	float velY;
	float angle;
	float angVel;

	public NetBodySyncEvent(int entityId, float x, float y, float velX, float velY, float angle, float angVel) {
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		this.angle = angle;
		this.angVel = angVel;
	}

	public NetBodySyncEvent() {

	}

	public NetBodySyncEvent setAndRet(int entityId, float x, float y, float velX, float velY, float angle, float angularVelocity) {
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		this.angle = angle;
		this.angVel = angularVelocity;
		return this;
	}

	public int getId() {
		return this.entityId;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getVelX() {
		return this.velX;
	}

	public float getVelY() {
		return this.velY;
	}

	/** Угол тела в радианах **/
	public float getAngle() {
		return angle;
	}

	public float getAngVel() {
		return angVel;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public void addVelX(float amount) {
		this.velX += amount;
	}

	public void addVelY(float amount) {
		this.velY += amount;
	}

	public void setPos(Vector2 pos){
		this.x = pos.x;
		this.y = pos.y;
	}

	public static NetBodySyncEvent fromBody(int id, @NotNull Body body){
		Vector2 position = body.getPosition();
		Vector2 linearVelocity = body.getLinearVelocity();
		return new NetBodySyncEvent(id, position.x, position.y, linearVelocity.x, linearVelocity.y, body.getAngle(), body.getAngularVelocity());
	}

	public void hardApply(Body body) {
		body.setTransform(x, y, angle);
		body.setLinearVelocity(velX, velY);
		body.setAngularVelocity(angVel);
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setAngVel(float angVel) {
		this.angVel = angVel;
	}

	@Override
	public String toString() {
		return "NetBodySyncEvent{" +
		"entityId=" + entityId +
		", x=" + x +
		", y=" + y +
		", velX=" + velX +
		", velY=" + velY +
		", angle=" + angle +
		", angVel=" + angVel +
		'}';
	}
}
