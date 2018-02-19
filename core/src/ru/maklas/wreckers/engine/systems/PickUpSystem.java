package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.*;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.events.AttachEvent;
import ru.maklas.wreckers.engine.events.requests.AttachRequest;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.PlayerPickUpZoneChangeRequest;

public class PickUpSystem extends EntitySystem implements EntityListener {

    @Override
    public void onAddedToEngine(final Engine engine) {
        engine.addListener(this);


        // Включаем/выключаем зону подбора для игрока
        subscribe(new Subscription<PlayerPickUpZoneChangeRequest>(PlayerPickUpZoneChangeRequest.class) {
            @Override
            public void receive(Signal<PlayerPickUpZoneChangeRequest> signal, PlayerPickUpZoneChangeRequest e) {
                Entity target = e.getEntity();
                WielderPickUpZoneComponent pickUp = target.get(Mappers.playerPickUpM);
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
        });

        // После того как оружие было установленно или наобарот изъято, нужно включить у оружия ранжу для подбирания
        subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
            @Override
            public void receive(Signal<AttachEvent> signal, AttachEvent e) {
                Entity weapon = e.getWeapon();
                WeaponPickUpComponent pickUpC = weapon.get(Mappers.weaponPickUpM);
                if (pickUpC == null){
                    return;
                }

                if (e.isAttached()){
                    disablePickUpZone(weapon, pickUpC);
                } else {
                    enablePickUpZone(weapon, pickUpC);
                }
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

                boolean attached = attachWeaponLogic(req.getWeapon(), weaponPC, req.getWeaponPickUp(), req.getWielder(), sockC);
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
                final Entity weaponToDetach;
                final SocketComponent socketC;

                switch (req.getType()){
                    case FIRST:

                        wielderDetachFrom = req.getWielder();
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        WSocket sock = socketC.firstAttached();
                        if (sock != null){
                            weaponToDetach = sock.attachedEntity;
                        } else {
                            return;
                        }

                        break;
                    case TARGET_ENTITY_AND_WEAPON:
                        wielderDetachFrom = req.getWielder();
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        weaponToDetach = req.getWeapon();
                        break;
                    case TARGET_WEAPON:
                        weaponToDetach = req.getWeapon();
                        wielderDetachFrom = weaponToDetach.get(Mappers.weaponPickUpM).wielder;
                        if (wielderDetachFrom == null){
                            return;
                        }
                        socketC = wielderDetachFrom.get(Mappers.socketM);
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + req.getType().name());
                }

                WeaponPickUpComponent wpu = weaponToDetach.get(Mappers.weaponPickUpM);
                PhysicsComponent wph = weaponToDetach.get(Mappers.physicsM);
                if (wpu != null && wph != null){
                    boolean success = detachWeaponLogic(weaponToDetach, wph, wpu, socketC);
                    if (success){
                        engine.dispatch(new AttachEvent(wielderDetachFrom, weaponToDetach, false));
                    }
                }
            }
        });
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    private void enablePickUpZone(Entity weapon, WeaponPickUpComponent wpu) {
        PhysicsComponent pc = weapon.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        wpu.fixture = pc.body.createFixture(wpu.def);
        wpu.fixture.setUserData(wpu);
    }

    private void disablePickUpZone(Entity weapon, WeaponPickUpComponent wpu){
        PhysicsComponent pc = weapon.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        if (wpu.fixture != null) {
            pc.body.destroyFixture(wpu.fixture);
            wpu.fixture = null;
        }
    }

    private boolean attachWeaponLogic(Entity weapon, PhysicsComponent weaponPC, WeaponPickUpComponent wpu, Entity player, SocketComponent psc){
        PhysicsComponent ppc = player.get(Mappers.physicsM);
        if (ppc == null){
            return false;
        }

        WSocket wSocket = psc.firstEmpty();

        if (wpu.attached || wSocket == null){
            return false;
        }
        boolean attach = wpu.attachAction.attach(player, wSocket, ppc.body);
        if (attach){
            wpu.attached = true;
            wpu.wielder = player;
            wSocket.attachedEntity = weapon;
            GameAssets.setFilterData(weaponPC.body, false, psc.weaponType);
            weapon.type = psc.weaponType.type;
        }
        return attach;
    }

    private boolean detachWeaponLogic(Entity weapon, PhysicsComponent wph, WeaponPickUpComponent wpu, SocketComponent psc){
        if (!wpu.attached) {
            return false;
        }

        WSocket socket = psc.find(weapon);
        if (socket == null){
            return false;
        }

        boolean success = wpu.attachAction.detach();
        if (success) {
            wpu.attached = false;
            wpu.wielder = null;
            socket.attachedEntity = null;

            GameAssets.setFilterData(wph.body, false, EntityType.NEUTRAL_WEAPON);
            weapon.type = EntityType.NEUTRAL_WEAPON.type;
        }
        return success;
    }



    @Override
    public void entityAdded(Entity entity) {
        // Если было заспавнено оружие без владельца, ему нужно придать зону для подбирания
        WeaponPickUpComponent wpu = entity.get(Mappers.weaponPickUpM);
        if (wpu != null && !wpu.attached && !wpu.enabled()){
            enablePickUpZone(entity, wpu);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
