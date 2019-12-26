package ru.maklas.wreckers.engine.weapon;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.net_events.NetAttachDetachEvent;
import ru.maklas.wreckers.net_events.NetDetachRequest;
import ru.maklas.wreckers.net_events.NetGrabZoneChange;
import ru.maklas.wreckers.net_events.NetGrabZoneChangeRequest;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;

/**
 * <p>
 *	 Подписывается и отвечает за ивенты:
 *	 <li>GrabZoneChangeRequest - Отвечает за уведомление Join'a</li>
 *	 <li>AttachEvent - Отвечает за уведомление Join'a</li>
 *	 <li>NetDetachRequest - Направляет во внутридвижковый ивент</li>
 *	 <li>NetGrabZoneChangeRequest - Направляет во внутридвижковый ивент</li>
 *
 *	 При этом генерируется AttachEvent и через сокет уведомляет второго игрока
 * </p>
 */
public class HostPickUpSystem extends DefaultPickUpSystem {

	private Socket socket;

	@Override
	public void onAddedToEngine(final Engine engine) {
		super.onAddedToEngine(engine);
		socket = engine.getBundler().get(B.socket);
		NetDispatcher netD = engine.getBundler().get(B.netD);

		subscribe(GrabZoneChangeRequest.class, this::onGrabZoneChangeReq);
		subscribe(AttachEvent.class, this::onAttachEvent);
		netD.subscribe(NetDetachRequest.class, this::onNetDetachReq);
		netD.subscribe(NetGrabZoneChangeRequest.class, this::onNetGrabZoneChangeReq);
	}


	//Получаем это от Join. Просто находим Entity и перенаправляем на локальный DetachEvent
	private void onNetDetachReq(Socket socket, NetDetachRequest req) {
		Entity player = engine.findById(req.getPlayerId());
		Entity weapon = engine.findById(req.getWeaponId());
		if (player == null || weapon == null){
			return;
		}
		engine.dispatch(new DetachRequest(DetachRequest.Type.TARGET_ENTITY_AND_WEAPON, player, weapon));
	}

	// Диспатчим что изменили GrabZone у кого-то
	private void onGrabZoneChangeReq(GrabZoneChangeRequest e) {
		socket.send(new NetGrabZoneChange(e.getEntity().id, e.state())); // Диспатчим что убрали
	}

	//Получаем это от Join. Просто находим Entity и перенаправляем на локальный GrabZoneChangeReq
	private void onNetGrabZoneChangeReq(Socket socket, NetGrabZoneChangeRequest req) {
		Entity entity = engine.findById(req.getPlayerId());
		if (entity != null) {
			engine.dispatch(new GrabZoneChangeRequest(req.getEnable(), entity));
		}
	}

	// Сообщаем другому клиенту об удачном привязывании/отвязывании итема
	private void onAttachEvent(AttachEvent e) {
		socket.send(new NetAttachDetachEvent(e.getOwner().id, e.getAttachable().id, e.isAttached()));
	}

	@Override
	public void update(float dt) {

	}
}
