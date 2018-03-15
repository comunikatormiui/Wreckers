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
import ru.maklas.wreckers.network.events.NetGrabZoneChange;

public class JoinPickUpSystem extends DefaultPickUpSystem {

    public JoinPickUpSystem(GameModel model) {
        super(model);
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        super.onAddedToEngine(engine);

        // По требованию сервера.
        subscribe(new Subscription<NetGrabZoneChange>(NetGrabZoneChange.class) {
            @Override
            public void receive(Signal<NetGrabZoneChange> signal, NetGrabZoneChange e) {
                Entity target = engine.getById(e.getEntityId());
                if (target == null){
                    return;
                }
                changeGrabZone(target, e.getState());
            }
        });

        //Тупо аттачим/детачим по требованию сервера. В обход AttachRequest/DetachRequest. Code duplication
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
    }


}
