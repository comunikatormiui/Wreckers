package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
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
import ru.maklas.wreckers.game.FixtureData;
import ru.maklas.wreckers.game.FixtureType;

public class PickUpSystem extends EntitySystem implements EntityListener {

    private final World world;

    public PickUpSystem(World world) {
        this.world = world;
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        engine.addListener(this);


        // Включаем/выключаем зону подбора для игрока
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
                    grabber.fixture.setUserData(new FixtureData(FixtureType.GRABBER_SENSOR));
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
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    private void createPickUpZone(Entity attachable, PickUpComponent pickUpC) {
        PhysicsComponent pc = attachable.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        Fixture fixture = pc.body.createFixture(pickUpC.def);
        fixture.setUserData(new FixtureData(FixtureType.PICKUP_SENSOR));
        pickUpC.fixture = fixture;
    }

    private void destroyPickUpZone(Entity attachable, PickUpComponent wpu){
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

        if (pickUpC.isAttached || wSocket == null){
            return false;
        }
        JointDef def = pickUpC.attachAction.attach(owner, wSocket, ownerPC.body);
        Joint joint = world.createJoint(def);

        pickUpC.isAttached = true;
        pickUpC.owner = owner;
        pickUpC.joint = joint;

        wSocket.attachedEntity = attachable;
        wSocket.joint = joint;

        GameAssets.setFilterData(attachablePC.body, false, ownerSocketC.weaponType); //TODO Больше не оружие. Фиксим
        attachable.type = ownerSocketC.weaponType.type;
        return true;
    }

    private boolean detachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, SocketComponent ownerSocketC){
        if (!pickUpC.isAttached) {
            return false;
        }

        WSocket socket = ownerSocketC.find(attachable);
        if (socket == null){
            return false;
        }

        Joint joint = pickUpC.joint;
        // Joint мог уже быть уничтожен к времени этого ивента, из-за уничтожения тела
        if (joint.getBodyA() != null && joint.getBodyB() != null){
            world.destroyJoint(joint);
        }
        pickUpC.isAttached = false;
        pickUpC.owner = null;
        pickUpC.joint = null;

        socket.attachedEntity = null;
        socket.joint = null;

        if (attachablePC.body != null) { //Тело уже могло быть уничтожено
            GameAssets.setFilterData(attachablePC.body, false, EntityType.NEUTRAL_WEAPON); //TODO Больше не оружие. Фиксим
        }
        attachable.type = EntityType.NEUTRAL_WEAPON.type;

        return true;
    }

    @Override
    public void entityAdded(Entity entity) {
        // Если было заспавнено оружие без владельца, ему нужно придать зону для подбирания
        PickUpComponent pickUpC = entity.get(Mappers.pickUpM);
        if (pickUpC != null && !pickUpC.isAttached && !pickUpC.pickUpZoneEnabled()){
            createPickUpZone(entity, pickUpC);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        PickUpComponent pickUpC = entity.get(Mappers.pickUpM);
        if (pickUpC != null && pickUpC.isAttached){
            PhysicsComponent pc = entity.get(Mappers.physicsM);
            Entity owner = pickUpC.owner;
            SocketComponent sc = owner.get(Mappers.socketM);
            if (pc != null && sc != null) {
                boolean success = detachLogic(entity, pc, pickUpC, sc);
                if (success){
                    getEngine().dispatch(new AttachEvent(owner, entity, false));
                }
            }
        }

        SocketComponent sc = entity.get(Mappers.socketM);
        if (sc != null) {
            for (WSocket socket : sc.sockets) {
                if (!socket.isEmpty()) {

                    Entity attached = socket.attachedEntity;

                    PhysicsComponent attachablePC = attached.get(Mappers.physicsM);
                    PickUpComponent attachedPickUp = attached.get(Mappers.pickUpM);
                    if (attachablePC != null && attachedPickUp != null) {
                        boolean success = detachLogic(attached, attachablePC, attachedPickUp, sc);
                        if (success){
                            getEngine().dispatch(new AttachEvent(entity, attached, false));
                        }
                    }
                }
            }
        }

    }
}
