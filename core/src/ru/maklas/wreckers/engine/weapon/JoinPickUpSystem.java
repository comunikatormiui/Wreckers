package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.wrecker.WSocketComponent;
import ru.maklas.wreckers.net_events.NetAttachDetachEvent;
import ru.maklas.wreckers.net_events.NetGrabZoneChange;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;

public class JoinPickUpSystem extends DefaultPickUpSystem {

	public JoinPickUpSystem() {
		super();
	}

	@Override
	public void onAddedToEngine(final Engine engine) {
		super.onAddedToEngine(engine);
		NetDispatcher netD = engine.getBundler().get(B.netD);

		// По требованию сервера.
		netD.subscribe(NetGrabZoneChange.class, this::onNetGrabZoneChange);

		//Тупо аттачим/детачим по требованию сервера. В обход AttachRequest/DetachRequest. Code duplication
		netD.subscribe(NetAttachDetachEvent.class, this::onNetAttachDetachEvent);
	}

	private void onNetAttachDetachEvent(Socket s, NetAttachDetachEvent e) {
		Entity owner = engine.findById(e.getPlayerId());
		Entity weapon = engine.findById(e.getWeaponId());
		if (owner == null || weapon == null){
			return;
		}

		PickUpComponent pickUp = weapon.get(M.pickUp);
		WSocketComponent sockC = owner.get(M.wSocket);
		PhysicsComponent weaponPC = weapon.get(M.physics);

		if (sockC == null || weaponPC == null || pickUp == null){
			return;
		}

		final boolean success;
		if (e.toAttach()) {
			success = attachLogic(weapon, weaponPC, pickUp, owner, sockC);
		} else {
			success = detachLogic(weapon, weaponPC, pickUp, sockC);
		}

		if (success){ // Диспатчим внутренний ивент для антиграва
			engine.dispatch(new AttachEvent(owner, weapon, e.toAttach()));
		}
	}

	private void onNetGrabZoneChange(Socket s, NetGrabZoneChange e) {
		Entity target = engine.findById(e.getEntityId());
		if (target == null){
			return;
		}
		changeGrabZone(target, e.getState());
	}

	@Override
	public void update(float dt) {

	}
}
