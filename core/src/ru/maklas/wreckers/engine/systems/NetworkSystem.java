package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.mnet.PingListener;
import ru.maklas.mnet.Socket;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.MotorComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.network.events.creation.*;
import ru.maklas.wreckers.network.events.sync.BodySyncEvent;
import ru.maklas.wreckers.network.events.sync.WreckerSyncEvent;

/**
 * <p>
 *     Subscribes and does actions for:
 *
 *     <li>BodySyncEvent</li>
 *     <li>WreckerSyncEvent</li>
 *     <li>WreckerCreationEvent</li>
 *     <li>SwordCreationEvent</li>
 *     <li>HammerCreationEvent</li>
 *     <li>ScytheCreationEvent</li>
 *     <li>PlatformCreationEvent</li>
 *
 *     <p>
 *         all methods can be overriden.
 *     <p>
 *         Some methods which start with 'sync' are left to be used in subclasses
 * </p>
 *
 */
public abstract class NetworkSystem extends EntitySystem {

    protected final GameModel model;
    protected final PingListener pl;

    public NetworkSystem(GameModel model) {
        this.model = model;
        pl = new PingListener() {
            @Override
            public void onPingUpdated(Socket socket, final float newPing) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        NetworkSystem.this.model.setPing(newPing);
                    }
                });
            }
        };
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        subscribeToBodyUpdate();
        subscribeToWreckerUpdate();
        subscribeToEntityCreationEvents();
        model.getSocket().addPingListener(pl);
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        model.getSocket().removePingListener(pl);
    }

    protected void subscribeToBodyUpdate(){
        subscribe(new Subscription<BodySyncEvent>(BodySyncEvent.class) {
            @Override
            public void receive(Signal<BodySyncEvent> signal, BodySyncEvent e) {
                Entity entity = getEngine().getById(e.getId());
                if (entity != null){
                    bodyUpdate(entity.get(Mappers.physicsM).body, e);
                }
            }
        });
    }

    protected void subscribeToWreckerUpdate(){
        subscribe(new Subscription<WreckerSyncEvent>(WreckerSyncEvent.class) {
            @Override
            public void receive(Signal<WreckerSyncEvent> signal, WreckerSyncEvent e) {

                Entity entity = getEngine().getById(e.getId());
                if (entity != null){
                    entity.get(Mappers.motorM).direction.set(e.getMotorX(), e.getMotorY());
                    bodyUpdate(entity.get(Mappers.physicsM).body, e.getPos());
                }

            }
        });
    }

    protected void bodyUpdate(Body body, BodySyncEvent e){
        smoothBodyUpdate(body, e);
    }

    protected void teleportBodyUpdate(Body body, BodySyncEvent e){
        e.hardApply(body);
    }

    final float maxDistance = 100;
    final float maxDistanceB2d = maxDistance / GameAssets.box2dScale;
    final float maxDistanceSquared = maxDistanceB2d * maxDistanceB2d;
    final float angleThreshold = 20;
    final float radAngleThreshold = angleThreshold * MathUtils.degreesToRadians;

    protected void smoothBodyUpdate(Body body, BodySyncEvent e){
        Vector2 targetPos = Utils.vec1.set(e.getX(), e.getY());
        Vector2 bodyPos = Utils.vec2.set(body.getPosition());
        final float distanceOverMax = (targetPos.dst2(body.getPosition())) / maxDistanceSquared;

        //Position
        if (distanceOverMax < 1){ //0..1 - Норма. небольшие коррекции
            final Vector2 directionToTarget = Utils.vec1.set(targetPos).sub(bodyPos); //Расстояние которое необходимо дополнительно пройти за 5 кадров.
            final Vector2 velocityToTarget = directionToTarget.scl(12);

            body.setLinearVelocity(e.getVelX() + velocityToTarget.x, e.getVelY() + velocityToTarget.y);
        } else {
            body.getTransform().setPosition(Utils.vec1.set(e.getX(), e.getY()));
            body.setLinearVelocity(e.getVelX(), e.getVelY());
        }


        final float angleDt = e.getAngle() - body.getAngle();
        if (Math.abs(angleDt) < radAngleThreshold){ //Угол отличается незначительно
            body.setAngularVelocity(e.getAngVel() + (angleDt * 12));
        } else {
            body.setTransform(body.getPosition(), e.getAngle());
            body.setAngularVelocity(e.getAngVel());
        }
    }

    private void subscribeToEntityCreationEvents(){
        subscribe(new Subscription<WreckerCreationEvent>(WreckerCreationEvent.class) {
            @Override
            public void receive(Signal<WreckerCreationEvent> signal, WreckerCreationEvent e) {
                onWreckerEvent(e);
            }
        });
        
        subscribe(new Subscription<SwordCreationEvent>(SwordCreationEvent.class) {
            @Override
            public void receive(Signal<SwordCreationEvent> signal, SwordCreationEvent e) {
                createSword(e);
            }
        });

        subscribe(new Subscription<HammerCreationEvent>(HammerCreationEvent.class) {
            @Override
            public void receive(Signal<HammerCreationEvent> signal, HammerCreationEvent e) {
                createHammer(e);
            }
        });

        subscribe(new Subscription<ScytheCreationEvent>(ScytheCreationEvent.class) {
            @Override
            public void receive(Signal<ScytheCreationEvent> signal, ScytheCreationEvent e) {
                createScythe(e);
            }
        });

        subscribe(new Subscription<PlatformCreationEvent>(PlatformCreationEvent.class) {
            @Override
            public void receive(Signal<PlatformCreationEvent> signal, PlatformCreationEvent e) {
                getEngine().add(new EntityPlatform(e.getId(), e.getX(), e.getY(), GameAssets.floorZ, e.getWidth(), e.getHeight(), model));
            }
        });
    }

    /**
     * Обрабатывает WreckerCreationEvent
     */
    protected void onWreckerEvent(WreckerCreationEvent e){
        Entity wrecker = createWrecker(e);
        getEngine().add(wrecker);
        onWreckerAdded(wrecker, e.isPlayer());
    }

    /**
     * Создаёт Wrecker
     */
    protected Entity createWrecker(WreckerCreationEvent e){
        EntityWrecker wrecker = new EntityWrecker(e.getId(), e.getX(), e.getY(), e.getHealth(), model, e.isPlayer() ? EntityType.PLAYER : EntityType.OPPONENT);
        wrecker.get(Mappers.wreckerM).set(e.getStats());
        return wrecker;
    }

    /**
     * Ивент о создании и занесении в model Wrecker'a
     */
    protected void onWreckerAdded(Entity wrecker, boolean isPlayer){
        if (isPlayer){
            model.setPlayer(wrecker);
        } else {
            model.setOpponent(wrecker);
        }
    }

    protected void createSword(SwordCreationEvent e) {
        getEngine().add(new EntitySword(e.getId(), e.getX(), e.getY(), GameAssets.swordZ, model));
    }

    protected void createHammer(HammerCreationEvent e) {
        getEngine().add(new EntityHammer(e.getId(), e.getX(), e.getY(), GameAssets.hammerZ, model));
    }

    protected void createScythe(ScytheCreationEvent e) {
        getEngine().add(new EntityScythe(e.getId(), e.getX(), e.getY(), GameAssets.scytheZ, model));
    }


    protected void sendSynchWeapon(Entity weapon){
        sendSyncBody(weapon);
    }

    protected void sendSynchWrecker(Entity wrecker){
        PhysicsComponent pc = wrecker.get(Mappers.physicsM);
        MotorComponent mc = wrecker.get(Mappers.motorM);

        if (pc != null && mc != null && pc.body != null) {
            BodySyncEvent bodySync = BodySyncEvent.fromBody(wrecker.id, pc.body);
            WreckerSyncEvent wreckerSyncEvent = new WreckerSyncEvent(bodySync, mc.direction.x, mc.direction.y);
            model.getSocket().send(wreckerSyncEvent);
        }
    }

    protected void sendSyncBody(Entity phycisEntity){
        PhysicsComponent pc = phycisEntity.get(Mappers.physicsM);

        if (pc != null && pc.body != null){
            BodySyncEvent syncEvent = BodySyncEvent.fromBody(phycisEntity.id, pc.body);
            model.getSocket().send(syncEvent);
        }
    }

}
