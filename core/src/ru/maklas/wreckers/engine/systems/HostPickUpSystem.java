package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.events.AttachEvent;
import ru.maklas.wreckers.engine.events.requests.AttachRequest;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.network.events.NetAttachDetachEvent;
import ru.maklas.wreckers.network.events.NetDetachRequest;
import ru.maklas.wreckers.network.events.NetGrabZoneChange;
import ru.maklas.wreckers.network.events.NetGrabZoneChangeRequest;

/**
 * <p>
 *     Подписывается и отвечает за ивенты:
 *     <li>GrabZoneChangeRequest - Отвечает за уведомление Join'a</li>
 *     <li>AttachEvent - Отвечает за уведомление Join'a</li>
 *     <li>NetDetachRequest - Направляет во внутридвижковый ивент</li>
 *     <li>NetGrabZoneChangeRequest - Направляет во внутридвижковый ивент</li>
 *
 *     При этом генерируется AttachEvent и через сокет уведомляет второго игрока
 * </p>
 */
public class HostPickUpSystem extends DefaultPickUpSystem {


    public HostPickUpSystem(GameModel model) {
        super(model);
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        super.onAddedToEngine(engine);


        // Диспатчим что изменили GrabZone у кого-то
        subscribe(new Subscription<GrabZoneChangeRequest>(GrabZoneChangeRequest.class) {
            @Override
            public void receive(Signal<GrabZoneChangeRequest> signal, GrabZoneChangeRequest e) {
                model.getSocket().send(new NetGrabZoneChange(e.getEntity().id, e.state())); // Диспатчим что убрали
            }
        });

        // Сообщаем другому клиенту об удачном привязывании/отвязывании итема
        subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
            @Override
            public void receive(Signal<AttachEvent> signal, AttachEvent e) {
                model.getSocket().send(new NetAttachDetachEvent(e.getOwner().id, e.getAttachable().id, e.isAttached()));
            }
        });

        //Получаем это от Join. Просто находим Entity и перенаправляем на локальный DetachEvent
        subscribe(new Subscription<NetDetachRequest>(NetDetachRequest.class) {
            @Override
            public void receive(Signal<NetDetachRequest> signal, NetDetachRequest e) {

                Entity player = engine.getById(e.getPlayerId());
                Entity weapon = engine.getById(e.getWeaponId());
                if (player == null || weapon == null){
                    return;
                }
                engine.dispatch(new DetachRequest(DetachRequest.Type.TARGET_ENTITY_AND_WEAPON, player, weapon));

            }
        });

        //Получаем это от Join. Просто находим Entity и перенаправляем на локальный GrabZoneChangeReq
        subscribe(new Subscription<NetGrabZoneChangeRequest>(NetGrabZoneChangeRequest.class) {
            @Override
            public void receive(Signal<NetGrabZoneChangeRequest> signal, NetGrabZoneChangeRequest e) {
                Entity entity = engine.getById(e.getPlayerId());
                if (entity != null) {
                    engine.dispatch(new GrabZoneChangeRequest(e.getEnable(), entity));
                }
            }
        });
    }

}
