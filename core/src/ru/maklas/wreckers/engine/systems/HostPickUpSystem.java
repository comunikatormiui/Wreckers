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
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.network.events.NetAttachDetachEvent;

/**
 * <p>
 *     Подписывается и отвечает за ивенты:
 *     <li>GrabZoneChangeRequest - Отвечает а включение/отключение зоны подбора при помощи реквеста (SuperClass).</li>
 *     <li>AttachRequest - Отвечает за присоединение оружия к игроку</li>
 *     <li>DetachRequest - Отвечает за изъятие оружие из сокета</li>
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

        // После того как оружие было установленно или наоборот изъято, нужно включить у оружия ранжу для подбирания
        //subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
        //    @Override
        //    public void receive(Signal<AttachEvent> signal, AttachEvent e) {
        //        Entity attachable = e.getAttachable();
        //        PickUpComponent pickUpC = attachable.get(Mappers.pickUpM);
        //        if (pickUpC == null){
        //            return;
        //        }

        //        if (e.isAttached()){
        //            destroyPickUpZone(attachable, pickUpC);
        //        } else {
        //            createPickUpZone(attachable, pickUpC);
        //        }
        //    }
        //});

        // Сообщаем другому клиенту об удачном привязывании итема
        subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
            @Override
            public void receive(Signal<AttachEvent> signal, AttachEvent e) {
                model.getSocket().send(new NetAttachDetachEvent(e.getOwner().id, e.getAttachable().id, e.isAttached()));
            }
        });

        // Attach request. Тут мы проверяем есть ли сокет, можно ли в целом прикрепить. Если да, то диспатчим success
        subscribe(new Subscription<AttachRequest>(AttachRequest.class) {
            @Override
            public void receive(Signal<AttachRequest> signal, AttachRequest req) {
                SocketComponent sockC = req.getWielder().get(Mappers.socketM);
                PhysicsComponent weaponPC = req.getWeapon().get(Mappers.physicsM);

                if (sockC == null && weaponPC == null){
                    return;
                }

                boolean attached = attachLogic(req.getWeapon(), weaponPC, req.getPickUp(), req.getWielder(), sockC);
                if (attached){
                    engine.dispatch(new AttachEvent(req.getWielder(), req.getWeapon(), true));
                }
            }
        });

        //DetachRequest. Тут мы проверяем можно ли изять у носителя оружие. Если можно, изымаем и диспатчим success
        subscribe(new Subscription<DetachRequest>(DetachRequest.class) {
            @Override
            public void receive(Signal<DetachRequest> signal, DetachRequest req) {

                final Entity wielderDetachFrom;
                final Entity entityToDetach;
                final SocketComponent socketC;

                switch (req.getType()){
                    case FIRST:

                        wielderDetachFrom = req.getWielder();
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        WSocket sock = socketC.firstAttached();
                        if (sock != null){
                            entityToDetach = sock.attachedEntity;
                        } else {
                            return;
                        }

                        break;
                    case TARGET_ENTITY_AND_WEAPON:
                        wielderDetachFrom = req.getWielder();
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        entityToDetach = req.getWeapon();
                        break;
                    case TARGET_WEAPON:
                        entityToDetach = req.getWeapon();
                        wielderDetachFrom = entityToDetach.get(Mappers.pickUpM).owner;
                        if (wielderDetachFrom == null){
                            return;
                        }
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + req.getType().name());
                }

                PickUpComponent pickUpC = entityToDetach.get(Mappers.pickUpM);
                PhysicsComponent wph = entityToDetach.get(Mappers.physicsM);
                if (pickUpC != null && wph != null){
                    boolean success = detachLogic(entityToDetach, wph, pickUpC, socketC);
                    if (success){
                        engine.dispatch(new AttachEvent(wielderDetachFrom, entityToDetach, false));
                    }
                }
            }
        });


        //Получаем это от Join. Просто находим Entity и перенаправляем на локальный DetachEvent
        subscribe(new Subscription<NetAttachDetachEvent>(NetAttachDetachEvent.class) {
            @Override
            public void receive(Signal<NetAttachDetachEvent> signal, NetAttachDetachEvent e) {
                if (e.toAttach()){ // У Join нет права удалённо прикреплять. Только откреплять.
                    Log.SERVER.warning("Join tried to attach a weapon. It works the other way");
                    return;
                }

                Entity player = engine.getById(e.getPlayerId());
                Entity weapon = engine.getById(e.getWeaponId());
                if (player == null || weapon == null){
                    return;
                }
                engine.dispatch(new DetachRequest(DetachRequest.Type.TARGET_ENTITY_AND_WEAPON, player, weapon));

            }
        });
    }

}
