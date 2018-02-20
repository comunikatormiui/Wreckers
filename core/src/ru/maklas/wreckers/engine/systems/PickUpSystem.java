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
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;

public class PickUpSystem extends EntitySystem implements EntityListener {

    @Override
    public void onAddedToEngine(final Engine engine) {
        engine.addListener(this);


        // ��������/��������� ���� ������� ��� ������
        subscribe(new Subscription<GrabZoneChangeRequest>(GrabZoneChangeRequest.class) {
            @Override
            public void receive(Signal<GrabZoneChangeRequest> signal, GrabZoneChangeRequest e) {
                Entity target = e.getEntity();
                GrabZoneComponent grabber = target.get(Mappers.grabM);
                if (grabber == null){
                    return;
                }
                if (e.state() && !grabber.enabled()){
                    PhysicsComponent pc = target.get(Mappers.physicsM);
                    if (pc == null){
                        return;
                    }
                    grabber.fixture = pc.body.createFixture(grabber.def);
                    grabber.fixture.setUserData(grabber);
                } else if (!e.state() && grabber.enabled()){
                    PhysicsComponent pc = target.get(Mappers.physicsM);
                    if (pc == null){
                        return;
                    }
                    pc.body.destroyFixture(grabber.fixture);
                    grabber.fixture = null;
                }
            }
        });

        // ����� ���� ��� ������ ���� ������������ ��� �������� ������, ����� �������� � ������ ����� ��� ����������
        subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
            @Override
            public void receive(Signal<AttachEvent> signal, AttachEvent e) {
                Entity attachable = e.getAttachable();
                PickUpComponent pickUpC = attachable.get(Mappers.pickUpM);
                if (pickUpC == null){
                    return;
                }

                if (e.isAttached()){
                    disablePickUpZone(attachable, pickUpC);
                } else {
                    enablePickUpZone(attachable, pickUpC);
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

                boolean attached = attachLogic(req.getWeapon(), weaponPC, req.getPickUp(), req.getWielder(), sockC);
                if (attached){
                    engine.dispatch(new AttachEvent(req.getWielder(), req.getWeapon(), true));
                }
            }
        });

        //DetachRequest. ��� �� ��������� ����� �� ����� � �������� ������. ���� �����, ������� � ��������� success
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
                        wielderDetachFrom = entityToDetach.get(Mappers.pickUpM).wielder;
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
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    private void enablePickUpZone(Entity attachable, PickUpComponent pickUpC) {
        PhysicsComponent pc = attachable.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        pickUpC.fixture = pc.body.createFixture(pickUpC.def);
        pickUpC.fixture.setUserData(pickUpC);
    }

    private void disablePickUpZone(Entity attachable, PickUpComponent wpu){
        PhysicsComponent pc = attachable.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        if (wpu.fixture != null) {
            pc.body.destroyFixture(wpu.fixture);
            wpu.fixture = null;
        }
    }

    private boolean attachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, Entity owner, SocketComponent ownerSocketC){
        PhysicsComponent ownerPC = owner.get(Mappers.physicsM);
        if (ownerPC == null){
            return false;
        }

        WSocket wSocket = ownerSocketC.firstEmpty();

        if (pickUpC.attached || wSocket == null){
            return false;
        }
        boolean attach = pickUpC.attachAction.attach(owner, wSocket, ownerPC.body);
        if (attach){
            pickUpC.attached = true;
            pickUpC.wielder = owner;
            wSocket.attachedEntity = attachable;
            GameAssets.setFilterData(attachablePC.body, false, ownerSocketC.weaponType);
            attachable.type = ownerSocketC.weaponType.type;
        }
        return attach;
    }

    private boolean detachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, SocketComponent ownerSocketC){
        if (!pickUpC.attached) {
            return false;
        }

        WSocket socket = ownerSocketC.find(attachable);
        if (socket == null){
            return false;
        }

        boolean success = pickUpC.attachAction.detach();
        if (success) {
            pickUpC.attached = false;
            pickUpC.wielder = null;
            socket.attachedEntity = null;

            GameAssets.setFilterData(attachablePC.body, false, EntityType.NEUTRAL_WEAPON); //TODO ������ �� ������. ������
            attachable.type = EntityType.NEUTRAL_WEAPON.type;
        }
        return success;
    }



    @Override
    public void entityAdded(Entity entity) {
        // ���� ���� ���������� ������ ��� ���������, ��� ����� ������� ���� ��� ����������
        PickUpComponent pickUpC = entity.get(Mappers.pickUpM);
        if (pickUpC != null && !pickUpC.attached && !pickUpC.enabled()){
            enablePickUpZone(entity, pickUpC);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
