package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.*;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.events.AttachEvent;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.network.events.NetGrabZoneChange;

/**
 * Набор методов для удачного удаления у прикрепления оружия к игроку.
 * Привязывается к GrabZoneChangeRequest и NetGrabZoneChange.
 * <p>
 * Использование:
 * Диспатчим GrabZoneChangeRequest. В движке тут же произойдёт изменение и другому клиенту отправится ивент.
 * Через какое-то время и у другого клиента произодёт смена состояния грабающей части
 */
public abstract class DefaultPickUpSystem extends EntitySystem implements EntityListener {

    protected final GameModel model;

    public DefaultPickUpSystem(GameModel model) {
        this.model = model;
    }


    @Override
    public void onAddedToEngine(final Engine engine) {
        engine.addListener(this);

        // Включаем/выключаем зону подбора для игрока. Ивента для Движка. Он уже диспатчит NetGrabZoneChange В сеть
        subscribe(new Subscription<GrabZoneChangeRequest>(GrabZoneChangeRequest.class) {
            @Override
            public void receive(Signal<GrabZoneChangeRequest> signal, GrabZoneChangeRequest e) {
                Entity target = e.getEntity();
                if (changeGrabZone(target, e.state())) {
                    model.getSocket().send(new NetGrabZoneChange(e.getEntity().id, e.state())); // Диспатчим что убрали
                }
            }
        });


        // Тупо Создаём, либо удаляем зону для грабания игрока
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
    }
    
    
    private boolean changeGrabZone(@NotNull Entity target, boolean enable){
        GrabZoneComponent grabber = target.get(Mappers.grabM);
        if (grabber == null){
            return false;
        }
        if (enable && !grabber.enabled()){
            PhysicsComponent pc = target.get(Mappers.physicsM);
            if (pc == null){
                return false;
            }
            grabber.fixture = pc.body.createFixture(grabber.def);
            grabber.fixture.setUserData(new FixtureData(FixtureType.GRABBER_SENSOR));
        } else if (!enable && grabber.enabled()){
            PhysicsComponent pc = target.get(Mappers.physicsM);
            if (pc == null){
                return false;
            }
            pc.body.destroyFixture(grabber.fixture);
            grabber.fixture = null;
        }
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


    @Override
    public void update(float dt) {
        super.update(dt);
    }

    protected void createPickUpZone(Entity attachable, PickUpComponent pickUpC) {
        PhysicsComponent pc = attachable.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        Fixture fixture = pc.body.createFixture(pickUpC.def);
        fixture.setUserData(new FixtureData(FixtureType.PICKUP_SENSOR));
        pickUpC.fixture = fixture;
    }

    protected void destroyPickUpZone(Entity attachable, PickUpComponent wpu){
        PhysicsComponent pc = attachable.get(Mappers.physicsM);
        if (pc == null){
            return;
        }
        if (wpu.fixture != null) {
            pc.body.destroyFixture(wpu.fixture);
            wpu.fixture = null;
        }
    }

    protected boolean attachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, Entity owner, SocketComponent ownerSocketC){
        PhysicsComponent ownerPC = owner.get(Mappers.physicsM);
        if (ownerPC == null){
            return false;
        }

        WSocket wSocket = ownerSocketC.firstEmpty();

        if (pickUpC.isAttached || wSocket == null){
            return false;
        }
        JointDef def = pickUpC.attachAction.attach(owner, wSocket, ownerPC.body);
        Joint joint = model.getWorld().createJoint(def);

        pickUpC.isAttached = true;
        pickUpC.owner = owner;
        pickUpC.joint = joint;

        wSocket.attachedEntity = attachable;
        wSocket.joint = joint;

        GameAssets.setFilterData(attachablePC.body, false, ownerSocketC.attachedEntityType); //TODO Больше не оружие. Фиксим
        attachable.type = ownerSocketC.attachedEntityType.type;
        return true;
    }

    protected boolean detachLogic(Entity attachable, PhysicsComponent attachablePC, PickUpComponent pickUpC, SocketComponent ownerSocketC){
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
            model.getWorld().destroyJoint(joint);
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
}
