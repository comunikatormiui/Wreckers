package ru.maklas.wreckers.engine.networking;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mnet2.NetBatch;
import ru.maklas.mnet2.PingListener;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.movemnet.MotorComponent;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.net_events.creation.*;
import ru.maklas.wreckers.net_events.sync.NetBodySyncEvent;
import ru.maklas.wreckers.net_events.sync.NetWreckerSyncEvent;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.statics.Layers;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.StringUtils;
import ru.maklas.wreckers.utils.Utils;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;

/**
 * <p>
 *	 Subscribes and does actions for:
 *
 *	 <li>NetBodySyncEvent</li>
 *	 <li>NetWreckerSyncEvent</li>
 *	 <li>NetWreckerCreationEvent</li>
 *	 <li>NetSwordCreationEvent</li>
 *	 <li>NetHammerCreationEvent</li>
 *	 <li>NetScytheCreationEvent</li>
 *	 <li>NetPlatformCreationEvent</li>
 *
 *	 <p>
 *		 all methods can be overriden.
 *	 <p>
 *		 Some methods which start with 'sync' are left to be used in subclasses
 * </p>
 *
 */
public abstract class NetworkSystem extends EntitySystem {

	private static final int SYNC_FRAME_FREQ = 5;
	private int framesBeforeNextSync = SYNC_FRAME_FREQ;
	protected final PingListener pl = (socket, ping) -> Log.trace("Ping: " + StringUtils.ff(ping) + " ms");
	protected Socket socket;

	@Override
	public void onAddedToEngine(Engine engine) {
		NetDispatcher netD = engine.getBundler().get(B.netD);
		netD.subscribe(NetBodySyncEvent.class, this::onBodySync);
		netD.subscribe(NetWreckerSyncEvent.class, this::onWreckerSync);
		netD.subscribe(NetWreckerCreationEvent.class, this::onWreckerEvent);
		netD.subscribe(NetSwordCreationEvent.class, this::createSword);
		netD.subscribe(NetHammerCreationEvent.class, this::createHammer);
		netD.subscribe(NetScytheCreationEvent.class, this::createScythe);
		netD.subscribe(NetPlatformCreationEvent.class, this::onNetPlatforCreationEvent);
		socket = engine.getBundler().get(B.socket);
		socket.addPingListener(pl);
	}

	@Override
	public void onRemovedFromEngine(Engine e) {
		socket.removePingListener(pl);
	}

	protected void bodyUpdate(Body body, NetBodySyncEvent e){
		smoothBodyUpdate(body, e);
	}

	protected void teleportBodyUpdate(Body body, NetBodySyncEvent e){
		e.hardApply(body);
	}

	final float maxDistance = 100;
	final float maxDistanceB2d = maxDistance / Game.scale;
	final float maxDistanceSquared = maxDistanceB2d * maxDistanceB2d;
	final float angleThreshold = 20;
	final float radAngleThreshold = angleThreshold * MathUtils.degreesToRadians;

	protected void smoothBodyUpdate(Body body, NetBodySyncEvent e){
		Vector2 targetPos = Utils.vec1.set(e.getX(), e.getY());
		Vector2 bodyPos = Utils.vec2.set(body.getPosition());
		final float distanceOverMax = (targetPos.dst2(body.getPosition())) / maxDistanceSquared;

		//Position
		if (distanceOverMax < 1){ //0..1 - Норма. небольшие коррекции
			final Vector2 directionToTarget = Utils.vec1.set(targetPos).sub(bodyPos); //Расстояние которое необходимо дополнительно пройти за 5 кадров.
			final Vector2 velocityToTarget = directionToTarget.scl(12);

			body.setLinearVelocity(e.getVelX() + velocityToTarget.x, e.getVelY() + velocityToTarget.y);
		} else {
			body.getTransform().setPosition(Utils.vec1.set(e.getX(), e.getY()));
			body.setLinearVelocity(e.getVelX(), e.getVelY());
		}


		final float angleDt = e.getAngle() - body.getAngle();
		if (Math.abs(angleDt) < radAngleThreshold){ //Угол отличается незначительно
			body.setAngularVelocity(e.getAngVel() + (angleDt * 12));
		} else {
			body.setTransform(body.getPosition(), e.getAngle());
			body.setAngularVelocity(e.getAngVel());
		}
	}

	/** Обрабатывает NetWreckerCreationEvent **/
	protected void onWreckerEvent(Socket s, NetWreckerCreationEvent e){
		Entity wrecker = createWrecker(e);
		getEngine().add(wrecker);
		onWreckerAdded(wrecker, e.isPlayer());
	}

	/** Создаёт Wrecker **/
	protected Entity createWrecker(NetWreckerCreationEvent e){
		int type = e.isPlayer() ? EntityType.PLAYER : EntityType.OPPONENT;
		EntityWrecker wrecker = new EntityWrecker(e.getId(), type, e.getX(), e.getY(), e.getHealth());
		wrecker.get(M.wrecker).set(e.getStats());
		return wrecker;
	}

	@Override
	public void update(float dt) {
		if (engine.getBundler().get(B.updateThisFrame)){
			engine.getBundler().set(B.updateThisFrame, false);
			framesBeforeNextSync = 0;
		}
		framesBeforeNextSync--;
		if (framesBeforeNextSync <= 0) {
			sync();
			framesBeforeNextSync = SYNC_FRAME_FREQ;
		}
	}

	protected abstract void sync();

	/** Ивент о создании и занесении в model Wrecker'a **/
	protected void onWreckerAdded(Entity wrecker, boolean isPlayer){
		if (isPlayer){
			engine.getBundler().set(B.player, wrecker);
		} else {
			engine.getBundler().set(B.opponent, wrecker);
		}
	}

	protected void createSword(Socket s, NetSwordCreationEvent e) {
		getEngine().add(new EntitySword(e.getId(), e.getX(), e.getY()));
	}

	protected void createHammer(Socket s, NetHammerCreationEvent e) {
		getEngine().add(new EntityHammer(e.getId(), e.getX(), e.getY()));
	}

	protected void createScythe(Socket s, NetScytheCreationEvent e) {
		getEngine().add(new EntityScythe(e.getId(), e.getX(), e.getY(), Layers.scytheZ));
	}


	protected void sendSynchWeapon(Entity weapon) {
		sendSyncBody(weapon);
	}

	protected void sendSynchWeapon(NetBatch batch, Entity weapon){
		sendSyncBody(batch, weapon);
	}

	protected void sendSynchWrecker(Entity wrecker){
		PhysicsComponent pc = wrecker.get(M.physics);
		MotorComponent mc = wrecker.get(M.motor);

		if (pc != null && mc != null && pc.body != null) {
			NetBodySyncEvent bodySync = NetBodySyncEvent.fromBody(wrecker.id, pc.body);
			NetWreckerSyncEvent wreckerSyncEvent = new NetWreckerSyncEvent(bodySync, mc.direction.x, mc.direction.y);
			socket.send(wreckerSyncEvent);
		}
	}

	protected void sendSyncBody(Entity phycisEntity){
		PhysicsComponent pc = phycisEntity.get(M.physics);

		if (pc != null && pc.body != null){
			NetBodySyncEvent syncEvent = NetBodySyncEvent.fromBody(phycisEntity.id, pc.body);
			socket.send(syncEvent);
		}
	}

	protected void sendSyncBody(NetBatch batch, Entity phycisEntity){
		PhysicsComponent pc = phycisEntity.get(M.physics);

		if (pc != null && pc.body != null){
			NetBodySyncEvent syncEvent = NetBodySyncEvent.fromBody(phycisEntity.id, pc.body);
			batch.add(syncEvent);
		}
	}

	private void onWreckerSync(Socket s, NetWreckerSyncEvent e1) {
		Entity entity = getEngine().findById(e1.getId());
		if (entity != null) {
			entity.get(M.motor).direction.set(e1.getMotorX(), e1.getMotorY());
			bodyUpdate(entity.get(M.physics).body, e1.getPos());
		}
	}

	private void onBodySync(Socket s, NetBodySyncEvent e1) {
		Entity entity = getEngine().findById(e1.getId());
		if (entity != null) {
			bodyUpdate(entity.get(M.physics).body, e1);
		}
	}

	private void onNetPlatforCreationEvent(Socket s, NetPlatformCreationEvent e) {
		getEngine().add(new EntityPlatform(e.getId(), e.getX(), e.getY(), Layers.floorZ, e.getWidth(), e.getHeight()));
	}
}
