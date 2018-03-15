package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.MotorComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.game.entities.EntityScythe;
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

    public NetworkSystem(GameModel model) {
        this.model = model;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        subscribeToBodyUpdate();
        subscribeToWreckerUpdate();
        subscribeToEntityCreationEvents();
    }

    protected void subscribeToBodyUpdate(){
        subscribe(new Subscription<BodySyncEvent>(BodySyncEvent.class) {
            @Override
            public void receive(Signal<BodySyncEvent> signal, BodySyncEvent e) {
                Entity entity = getEngine().getById(e.getId());
                if (entity != null){
                    e.hardApply(entity.get(Mappers.physicsM).body);
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
                    e.getPos().hardApply(entity.get(Mappers.physicsM).body);
                }

            }
        });
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
