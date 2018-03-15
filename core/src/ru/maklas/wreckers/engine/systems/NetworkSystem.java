package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.Gdx;
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
import ru.maklas.wreckers.game.entities.EntityScythe;
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
        teleportBodyUpdate(body, e);
    }

    protected void teleportBodyUpdate(Body body, BodySyncEvent e){
        e.hardApply(body);
    }

    protected void teleportBodyUpdate(Body body, float x, float y, float vX, float vY, float angle){
        body.setTransform(x, y, angle);
        body.setLinearVelocity(vX, vY);
    }

    protected void changeVelocity(Body body, float vX, float vY, float angle){
        body.setTransform(body.getPosition(), angle);
        body.setLinearVelocity(vX, vY);
    }

    Vector2 tempVec = new Vector2();
    protected void smoothBodyUpdate(Body body, BodySyncEvent e){
        final float maxDistance = 20f;
        final float maxDistanceB2d = (maxDistance / GameAssets.box2dScale);
        final float maxDistanceSquared = maxDistanceB2d * maxDistanceB2d;


        Vector2 targetPos = Utils.vec1.set(e.getX(), e.getY());
        Vector2 bodyPos = Utils.vec2.set(body.getPosition());
        final float distanceOverMax = (targetPos.dst2(body.getPosition())) / maxDistanceSquared;


        if (distanceOverMax >= 4){        // 4..∞ - Гораздо выше нормы. Возможно произошёл телепорт на сервере. Хардово телепортируем.
            teleportBodyUpdate(body, e);

        } else if (distanceOverMax > 1){  // 1..4 - Выше нормы. Расстояние между телом и ивентом больше чем максимально доступное в 1..2 раза. Рекомендуется телепортировать на половину этого расстояния и придать ускорения.
            Vector2 middlePos = tempVec.set(bodyPos).lerp(targetPos, 0.5f);
            teleportBodyUpdate(body, middlePos.x, middlePos.y, e.getVelX() * 2, e.getVelY() * 2, e.getAngle());
        } else {                          // 0..1 - Норма. Означает что расстояние от текущего положения до ивента сервера незначительно. Достаточно придать немного ускорения.
            float mul = 1 + distanceOverMax;
            changeVelocity(body, e.getVelX() * mul, e.getVelY() * mul, e.getAngle());
        }
    }

    protected void smoothBodyUpdate2(Body body, BodySyncEvent e){
        final float maxDistance = 10;
        final float maxDistanceB2d = (maxDistance / GameAssets.box2dScale);
        final float maxDistanceSquared = maxDistanceB2d * maxDistanceB2d;


        Vector2 targetPos = Utils.vec1.set(e.getX(), e.getY());
        Vector2 bodyPos = Utils.vec2.set(body.getPosition());
        final float distanceOverMax = (targetPos.dst2(body.getPosition())) / maxDistanceSquared;


        if (distanceOverMax < 1){ //0..1 - Норма. небольшие коррекции
            //TODO: получить направление движения и скорость с которой нужно двигаться.
            final Vector2 directionToTarget = new Vector2(targetPos).sub(bodyPos);
            final Vector2 vel = new Vector2(body.getLinearVelocity());
            final Vector2 velToTarget = new Vector2(directionToTarget).scl(0.1f);
            final Vector2 newVel = new Vector2(vel).add(velToTarget);
            body.setLinearVelocity(newVel);
        } else {
            teleportBodyUpdate(body, e);
            System.err.println("Jump");
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
                getEngine().add(new ru.maklas.wreckers.game.entities.EntityPlatform(e.getId(), e.getX(), e.getY(), GameAssets.floorZ, e.getWidth(), e.getHeight(), model));
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
        ru.maklas.wreckers.game.entities.EntityWrecker wrecker = new ru.maklas.wreckers.game.entities.EntityWrecker(e.getId(), e.getX(), e.getY(), e.getHealth(), model, e.isPlayer() ? EntityType.PLAYER : EntityType.OPPONENT);
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
        getEngine().add(new ru.maklas.wreckers.game.entities.EntitySword(e.getId(), e.getX(), e.getY(), GameAssets.swordZ, model));
    }

    protected void createHammer(HammerCreationEvent e) {
        getEngine().add(new ru.maklas.wreckers.game.entities.EntityHammer(e.getId(), e.getX(), e.getY(), GameAssets.hammerZ, model));
    }

    protected void createScythe(ScytheCreationEvent e) {
        getEngine().add(new EntityScythe(e.getId(), e.getX(), e.getY(), GameAssets.scytheZ, model));
    }


    protected void sendSynchWeapon(Entity weapon){
        syncBody(weapon);
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

    protected void syncBody(Entity phycisEntity){
        PhysicsComponent pc = phycisEntity.get(Mappers.physicsM);

        if (pc != null && pc.body != null){
            BodySyncEvent syncEvent = BodySyncEvent.fromBody(phycisEntity.id, pc.body);
            model.getSocket().send(syncEvent);
        }
    }

}
