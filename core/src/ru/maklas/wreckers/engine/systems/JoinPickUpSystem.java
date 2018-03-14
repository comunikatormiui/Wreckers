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

                if (e.toAttach()) {
                    attachLogic(weapon, weaponPC, pickUp, owner, sockC);
                } else {
                    detachLogic(weapon, weaponPC, pickUp, sockC);
                }
            }
        });
    }
}
