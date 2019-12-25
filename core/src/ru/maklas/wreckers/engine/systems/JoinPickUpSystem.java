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
        subscribe(NetGrabZoneChange.class, e -> {
                Entity target = engine.findById(e.getEntityId());
                if (target == null){
                    return;
                }
                changeGrabZone(target, e.getState());
        });

        //Тупо аттачим/детачим по требованию сервера. В обход AttachRequest/DetachRequest. Code duplication
        subscribe(NetAttachDetachEvent.class, e -> {
                Entity owner = engine.findById(e.getPlayerId());
                Entity weapon = engine.findById(e.getWeaponId());
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
        });
    }

    @Override
    public void update(float dt) {

    }
}
