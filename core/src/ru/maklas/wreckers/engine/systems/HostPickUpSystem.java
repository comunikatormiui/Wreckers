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
        subscribe(GrabZoneChangeRequest.class, e -> {
                model.getSocket().send(new NetGrabZoneChange(e.getEntity().id, e.state())); // Диспатчим что убрали
        });

        // Сообщаем другому клиенту об удачном привязывании/отвязывании итема
        subscribe(AttachEvent.class, e -> {
                model.getSocket().send(new NetAttachDetachEvent(e.getOwner().id, e.getAttachable().id, e.isAttached()));
            });

        //Получаем это от Join. Просто находим Entity и перенаправляем на локальный DetachEvent
        subscribe(NetDetachRequest.class, e -> {

                Entity player = engine.findById(e.getPlayerId());
                Entity weapon = engine.findById(e.getWeaponId());
                if (player == null || weapon == null){
                    return;
                }
                engine.dispatch(new DetachRequest(DetachRequest.Type.TARGET_ENTITY_AND_WEAPON, player, weapon));

            });

        //Получаем это от Join. Просто находим Entity и перенаправляем на локальный GrabZoneChangeReq
        subscribe(NetGrabZoneChangeRequest.class, e -> {
                Entity entity = engine.findById(e.getPlayerId());
                if (entity != null) {
                    engine.dispatch(new GrabZoneChangeRequest(e.getEnable(), entity));
                }
            });
    }


    @Override
    public void update(float dt) {

    }
}
