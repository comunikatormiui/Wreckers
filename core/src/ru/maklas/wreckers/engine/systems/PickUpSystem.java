package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.*;
import ru.maklas.mengine.utils.Listener;
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


        // ��������/��������� ���� ������� ��� ������
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

        // ����� ���� ��� ������ ���� ������������ ��� �������� ������, ����� �������� � ������ ����� ��� ����������
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

        // Attach request. ��� �� ��������� ���� �� �����, ����� �� � ����� ����������. ���� ��, �� ��������� success
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

        //DetachRequest. ��� �� ��������� ����� �� ����� � �������� ������. ���� �����, ������� � ��������� success
        subscribe(new Subscription<DetachRequest>(DetachRequest.class) {
            @Override
            public void receive(Signal<DetachRequest> signal, DetachRequest req) {
                Entity wielder = req.getWielder();
                SocketComponent socketC = wielder.get(Mappers.socketM);
                if (socketC == null) {
                    return;
                }

                final Entity weaponToDetach;

                switch (req.getType()){
                    case FIRST:

                        WSocket sock = socketC.firstAttached();
                        if (sock != null){
                            weaponToDetach = sock.attachedEntity;
                        } else {
                            return;
                        }

                        break;
                    case TARGET:
                        if (req.getWeapon() == null){
                            throw new RuntimeException("Weapon must not be null at this point");
                        } else {
                            weaponToDetach = req.getWeapon();
                        }
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + req.getType().name());
                }

                WeaponPickUpComponent wpu = weaponToDetach.get(Mappers.weaponPickUpM);
                PhysicsComponent wph = weaponToDetach.get(Mappers.physicsM);
                if (wpu != null && wph != null){
                    boolean success = detachWeaponLogic(weaponToDetach, wph, wpu, socketC);
                    if (success){
                        engine.dispatch(new AttachEvent(wielder, weaponToDetach, false));
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
        }
        return success;
    }



    @Override
    public void entityAdded(Entity entity) {
        // ���� ���� ���������� ������ ��� ���������, ��� ����� ������� ���� ��� ����������
        WeaponPickUpComponent wpu = entity.get(Mappers.weaponPickUpM);
        if (wpu != null && !wpu.attached && !wpu.enabled()){
            enablePickUpZone(entity, wpu);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
