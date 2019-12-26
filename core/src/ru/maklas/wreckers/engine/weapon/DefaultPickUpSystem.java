package ru.maklas.wreckers.engine.weapon;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntityListener;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.wrecker.WSocket;
import ru.maklas.wreckers.engine.wrecker.WSocketComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;

/** Набор методов для удачного удаления у прикрепления оружия к игроку, А так же смене GrabZone **/
public abstract class DefaultPickUpSystem extends EntitySystem implements EntityListener {

	@Override
	public void onAddedToEngine(final Engine engine) {
		engine.addListener(this);

		subscribe(GrabZoneChangeRequest.class, this::onGrabZoneChangeRequest);
		subscribe(AttachRequest.class, this::onAttachRequest);
		subscribe(DetachRequest.class, this::onDetachRequest);
	}

	private void onDetachRequest(DetachRequest req) {
		final Entity wielderDetachFrom;
		final Entity entityToDetach;
		final WSocketComponent socketC;

		switch (req.getType()){
			case FIRST:

				wielderDetachFrom = req.getWielder();
				socketC = wielderDetachFrom.get(M.wSocket);
				WSocket sock = socketC.firstAttached();
				if (sock != null){
					entityToDetach = sock.attachedEntity;
				} else {
					return;
				}

				break;
			case TARGET_ENTITY_AND_WEAPON:
				wielderDetachFrom = req.getWielder();
				socketC = wielderDetachFrom.get(M.wSocket);
				entityToDetach = req.getWeapon();
				break;
			case TARGET_WEAPON:
				entityToDetach = req.getWeapon();
				wielderDetachFrom = entityToDetach.get(M.pickUp).owner;
				if (wielderDetachFrom == null){
					return;
				}
				socketC = wielderDetachFrom.get(M.wSocket);
				break;
			default:
				throw new RuntimeException("Unknown type: " + req.getType().name());
		}

		PickUpComponent pickUpC = entityToDetach.get(M.pickUp);
		PhysicsComponent wph = entityToDetach.get(M.physics);
		if (pickUpC != null && wph != null){
			boolean success = detachLogic(entityToDetach, wph, pickUpC, socketC);
			if (success){
				engine.dispatch(new AttachEvent(wielderDetachFrom, entityToDetach, false));
			}
		}
	}

	private void onAttachRequest(AttachRequest req) {
		WSocketComponent sockC = req.getWielder().get(M.wSocket);
		PhysicsComponent weaponPC = req.getWeapon().get(M.physics);

		if (sockC == null && weaponPC == null){
			return;
		}

		boolean attached = attachLogic(req.getWeapon(), weaponPC, req.getPickUp(), req.getWielder(), sockC);
		if (attached){
			engine.dispatch(new AttachEvent(req.getWielder(), req.getWeapon(), true));
		}
	}


	protected boolean changeGrabZone(@NotNull Entity target, boolean enable){
		GrabZoneComponent grabber = target.get(M.grab);
		if (grabber == null){
			return false;
		}
		if (enable && !grabber.enabled()){
			PhysicsComponent pc = target.get(M.physics);
			if (pc == null){
				return false;
			}
			grabber.fixture = pc.body.createFixture(grabber.def);
			grabber.fixture.setUserData(new FixtureData(FixtureType.GRABBER_SENSOR));
		} else if (!enable && grabber.enabled()){
			PhysicsComponent pc = target.get(M.physics);
			if (pc == null){
				return false;
			}
			pc.body.destroyFixture(grabber.fixture);
			grabber.fixture = null;
		}
		return true;
	}

	@Override
	public void entityAdded(Entity entity) {
		// Если было заспавнено оружие без владельца, ему нужно придать зону для подбирания
		PickUpComponent pickUpC = entity.get(M.pickUp);
		if (pickUpC != null && !pickUpC.isAttached && !pickUpC.pickUpZoneEnabled()){
			createPickUpZone(entity, pickUpC);
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		PickUpComponent pickUpC = entity.get(M.pickUp);
		if (pickUpC != null && pickUpC.isAttached){
			PhysicsComponent pc = entity.get(M.physics);
			Entity owner = pickUpC.owner;
			WSocketComponent sc = owner.get(M.wSocket);
			if (pc != null && sc != null) {
				boolean success = detachLogic(entity, pc, pickUpC, sc);
				if (success){
					getEngine().dispatch(new AttachEvent(owner, entity, false));
				}
			}
		}

		WSocketComponent sc = entity.get(M.wSocket);
		if (sc != null) {
			for (WSocket socket : sc.sockets) {
				if (!socket.isEmpty()) {

					Entity attached = socket.attachedEntity;

					PhysicsComponent attachablePC = attached.get(M.physics);
					PickUpComponent attachedPickUp = attached.get(M.pickUp);
					if (attachablePC != null && attachedPickUp != null) {
						boolean success = detachLogic(attached, attachablePC, attachedPickUp, sc);
						if (success){
							getEngine().dispatch(new AttachEvent(entity, attached, false));
						}
					}
				}
			}
		}

	}


	protected void createPickUpZone(Entity attachable, PickUpComponent pickUpC) {
		PhysicsComponent pc = attachable.get(M.physics);
		if (pc == null){
			return;
		}
		Fixture fixture = pc.body.createFixture(pickUpC.def);
		fixture.setUserData(new FixtureData(FixtureType.PICKUP_SENSOR));
		pickUpC.fixture = fixture;
	}

	protected void destroyPickUpZone(Entity attachable, PickUpComponent wpu){
		PhysicsComponent pc = attachable.get(M.physics);
		if (pc == null){
			return;
		}
		if (wpu.fixture != null) {
			pc.body.destroyFixture(wpu.fixture);
			wpu.fixture = null;
		}
	}

	protected boolean attachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, Entity owner, WSocketComponent ownerSocketC){
		PhysicsComponent ownerPC = owner.get(M.physics);
		if (ownerPC == null){
			return false;
		}

		WSocket wSocket = ownerSocketC.firstEmpty();

		if (pickUpC.isAttached || wSocket == null){
			return false;
		}
		JointDef def = pickUpC.attachAction.attach(owner, wSocket, ownerPC.body);
		Joint joint = A.physics.world.createJoint(def);

		pickUpC.isAttached = true;
		pickUpC.owner = owner;
		pickUpC.joint = joint;

		wSocket.attachedEntity = attachable;
		wSocket.joint = joint;

		Game.setFilterData(attachablePC.body, false, EntityType.of(ownerSocketC.attachedEntityType)); //TODO Больше не оружие. Фиксим
		attachable.type = ownerSocketC.attachedEntityType;
		return true;
	}

	protected boolean detachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, WSocketComponent ownerSocketC){
		if (!pickUpC.isAttached) {
			return false;
		}

		WSocket socket = ownerSocketC.find(attachable);
		if (socket == null){
			return false;
		}

		Joint joint = pickUpC.joint;
		// Joint мог уже быть уничтожен к времени этого ивента, из-за уничтожения тела
		if (joint.getBodyA() != null && joint.getBodyB() != null){
			A.physics.world.destroyJoint(joint);
		}
		pickUpC.isAttached = false;
		pickUpC.owner = null;
		pickUpC.joint = null;

		socket.attachedEntity = null;
		socket.joint = null;

		if (attachablePC.body != null) { //Тело уже могло быть уничтожено
			Game.setFilterData(attachablePC.body, false, EntityType.of(EntityType.NEUTRAL_WEAPON)); //TODO Больше не оружие. Фиксим
		}
		attachable.type = EntityType.NEUTRAL_WEAPON;

		return true;
	}

	// Включаем/выключаем зону подбора для игрока.
	private void onGrabZoneChangeRequest(GrabZoneChangeRequest e) {
		Entity target = e.getEntity();
		changeGrabZone(target, e.state());
	}
}
