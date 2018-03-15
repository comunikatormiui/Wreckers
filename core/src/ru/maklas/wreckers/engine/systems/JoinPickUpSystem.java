package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.components.SocketComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.engine.events.AttachEvent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.network.events.NetAttachDetachEvent;

public class JoinPickUpSystem extends DefaultPickUpSystem {

    public JoinPickUpSystem(GameModel model) {
        super(model);
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        super.onAddedToEngine(engine);


        //Тупо аттачим/детачим по ивенту из инета
        subscribe(new Subscription<NetAttachDetachEvent>(NetAttachDetachEvent.class) {
            @Override
            public void receive(Signal<NetAttachDetachEvent> signal, NetAttachDetachEvent e) {
                Entity owner = engine.getById(e.getPlayerId());
                Entity weapon = engine.getById(e.getWeaponId());
                if (owner == null || weapon == null){
                    return;
                }

                PickUpComponent pickUp = weapon.get(Mappers.pickUpM);
                SocketComponent sockC = owner.get(Mappers.socketM);
                PhysicsComponent weaponPC = weapon.get(Mappers.physicsM);

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
        });


        //Отправляем AttachDetachRequest на сервер. Получаем такой же ивент после валидации и уже тогда открепляем.
        subscribe(new Subscription<DetachRequest>(DetachRequest.class) {
            @Override
            public void receive(Signal<DetachRequest> signal, DetachRequest req) {

                final Entity wielderDetachFrom;
                final Entity entityToDetach;

                switch (req.getType()){
                    case FIRST:

                        wielderDetachFrom = req.getWielder();
                        if (wielderDetachFrom != model.getPlayer()){ //У Join игрока есть возможность детатчить только от себя
                            return;
                        }
                        SocketComponent socketC = wielderDetachFrom.get(Mappers.socketM);
                        WSocket sock = socketC.firstAttached();
                        if (sock != null){
                            entityToDetach = sock.attachedEntity;
                        } else {
                            return;
                        }
                        break;
                    case TARGET_ENTITY_AND_WEAPON:
                        wielderDetachFrom = req.getWielder();
                        entityToDetach = req.getWeapon();
                        break;
                    case TARGET_WEAPON:
                        entityToDetach = req.getWeapon();
                        wielderDetachFrom = entityToDetach.get(Mappers.pickUpM).owner;
                        if (wielderDetachFrom == null){
                            return;
                        }
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + req.getType().name());
                }

                model.getSocket().send(new NetAttachDetachEvent(wielderDetachFrom.id, entityToDetach.id, false));
            }
        });
    }


}
