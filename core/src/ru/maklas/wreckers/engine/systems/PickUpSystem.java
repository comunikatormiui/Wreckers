package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.Fixture;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.events.WeaponPickUpEvent;
import ru.maklas.wreckers.engine.events.requests.PlayerPickUpZoneChangeRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponPickUpRequest;

public class PickUpSystem extends EntitySystem {

    Listener<PlayerPickUpZoneChangeRequest> listener;

    @Override
    public void onAddedToEngine(final Engine engine) {

        listener = new Listener<PlayerPickUpZoneChangeRequest>() {
            @Override
            public void receive(Signal<PlayerPickUpZoneChangeRequest> signal, PlayerPickUpZoneChangeRequest e) {
                Entity target = e.getEntity();
                PlayerPickUpComponent pickUp = target.get(Mappers.playerPickUpM);
                if (pickUp == null){
                    return;
                }
                if (e.state() && !pickUp.enabled()){
                    PhysicsComponent pc = target.get(Mappers.physicsM);
                    if (pc == null){
                        return;
                    }
                    pickUp.fixture = pc.body.createFixture(pickUp.def);
                    pickUp.fixture.setUserData(pickUp);
                } else if (!e.state() && pickUp.enabled()){
                    PhysicsComponent pc = target.get(Mappers.physicsM);
                    if (pc == null){
                        return;
                    }
                    pc.body.destroyFixture(pickUp.fixture);
                    pickUp.fixture = null;
                }
            }
        };
        engine.subscribe(PlayerPickUpZoneChangeRequest.class, listener);

        engine.subscribe(WeaponPickUpEvent.class, new Listener<WeaponPickUpEvent>() {
            @Override
            public void receive(Signal<WeaponPickUpEvent> signal, WeaponPickUpEvent e) {
                Entity weapon = e.getWeapon();
                WeaponPickUpComponent pickUpC = weapon.get(Mappers.weaponPickUpM);
                PhysicsComponent pc = weapon.get(Mappers.physicsM);

                if (e.isPickedUp()){
                    if (pickUpC != null && pc != null && pickUpC.enabled()){
                        pc.body.destroyFixture(pickUpC.fixture);
                        pickUpC.fixture = null;
                    }
                } else {
                    if (pickUpC != null && pc != null && !pickUpC.enabled()) {
                        Fixture fixture = pc.body.createFixture(pickUpC.def);
                        pickUpC.fixture = fixture;
                        fixture.setUserData(pickUpC);
                    }
                }
            }
        });

        engine.subscribe(WeaponPickUpRequest.class, new Listener<WeaponPickUpRequest>() {
            @Override
            public void receive(Signal<WeaponPickUpRequest> signal, WeaponPickUpRequest req) {
                WeaponSocketComponent sockC = req.getWielder().get(Mappers.socketM);
                PhysicsComponent playerPc = req.getWielder().get(Mappers.physicsM);
                if (sockC != null && playerPc != null){
                    WSocket wSocket = sockC.firstEmpty();
                    if (wSocket != null){
                        req.getWeaponPickUp().attachAction.attach(req.getWielder(), wSocket, playerPc.body);
                    }
                }
            }
        });
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void removeFromEngine() {
        getEngine().unsubscribe(PlayerPickUpZoneChangeRequest.class, listener);
    }
}
