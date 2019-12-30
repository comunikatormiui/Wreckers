package ru.maklas.wreckers.net_events.sync;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.jetbrains.annotations.NotNull;
import ru.maklas.wreckers.utils.Utils;
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

	/**
	 * @param smoothness Отвечает за скорость с которой будет преодолеваться разница в соединении.
	 *                   от 0 до 1. Чем лучше соединение и чем больше важна позиция Entity,
	 *                   тем больше стоит выставлять данное значение.
	 * @param updateRate Как часто приходят обновления в секундах
	 * @param radAngleThreshold Максимальная разница в повороте (радиан) после которой Body просто будет телепортировано
	 * @param maxDistanceSquared Квадрат максимального расстояния. После которого Body просто будет телепортировано.
	 */
	public void smoothApply(Body body, float smoothness, float updateRate, float radAngleThreshold, float maxDistanceSquared){
		float futureScalar = smoothness / updateRate;
		Vector2 targetPos = Utils.vec1.set(x, y);
		Vector2 bodyPos = Utils.vec2.set(body.getPosition());
		final float distanceOverMax = (targetPos.dst2(body.getPosition())) / maxDistanceSquared;

		//Position
		if (distanceOverMax < 1){ //0..1 - Норма. небольшие коррекции
			final Vector2 directionToTarget = Utils.vec1.set(targetPos).sub(bodyPos); //Расстояние которое необходимо дополнительно пройти за 5 кадров.
			final Vector2 velocityToTarget = directionToTarget.scl(futureScalar);

			body.setLinearVelocity(velX + velocityToTarget.x, velY + velocityToTarget.y);
		} else {
			body.setTransform(Utils.vec1.set(x, y), angle);
			body.setLinearVelocity(velX, velY);
		}


		final float angleDt = angle - body.getAngle();
		if (Math.abs(angleDt) < radAngleThreshold){ //Угол отличается незначительно
			body.setAngularVelocity(angVel + (angleDt * futureScalar * 0.1f));
		} else {
			body.setTransform(body.getPosition(), angle);
			body.setAngularVelocity(angVel);
		}
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
