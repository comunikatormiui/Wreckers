package ru.maklas.wreckers.engine.systems;

import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.Masks;
import ru.maklas.wreckers.client.entities.EntityPistolBullet;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.CollisionComponent;
import ru.maklas.wreckers.engine.components.PlayerInventoryComponent;
import ru.maklas.wreckers.engine.components.ShooterComponent;
import ru.maklas.wreckers.engine.events.ShootEvent;
import ru.maklas.wreckers.game.Box2dModel;
import ru.maklas.wreckers.game.WeaponType;
import ru.maklas.wreckers.game.weapons.PistolWeapon;

public class ShooterSystem extends EntitySystem {

    private final Box2dModel model;
    private ImmutableArray<Entity> shooters;
    private Listener<ShootEvent> listener;
    private ImmutableArray<Entity> entitiesWithCollision;

    public ShooterSystem(Box2dModel model) {
        this.model = model;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        shooters = engine.entitiesFor(ShooterComponent.class);
        entitiesWithCollision = engine.entitiesFor(CollisionComponent.class);
        listener = new Listener<ShootEvent>() {
            @Override
            public void receive(Signal<ShootEvent> signal, ShootEvent shootEvent) {
                Entity shooter = shootEvent.getShooter();
                ShooterComponent sc = shooter.get(Mappers.shooterM);
                if (sc == null) {
                    return;
                }
                PlayerInventoryComponent bc = shooter.get(Mappers.inventoryM);
                if (bc == null) {
                    return;
                }

                shoot(shooter, sc, bc);

            }
        };
        engine.subscribe(ShootEvent.class, listener);
    }

    private void shoot(@NotNull Entity shooter, @NotNull ShooterComponent sc, @NotNull PlayerInventoryComponent bc) {
        if (shooter.type == Masks.PLAYER.type){
            shootAsPlayer(shooter, sc, bc);
        }
    }

    private void shootAsPlayer(Entity shooter, ShooterComponent sc, PlayerInventoryComponent bc) {

        if (bc.currentWeapon.type == WeaponType.PISTOL){
            shootPlayerPistol(shooter, sc, (PistolWeapon) bc.currentWeapon);
        }
    }

    private void shootPlayerPistol(Entity shooter, ShooterComponent sc, PistolWeapon weapon) {
        if (!weapon.canDec(1)){
            return;
        }

        weapon.decAmmo(1);
        EntityPistolBullet bullet = new EntityPistolBullet( sc.shootingPoint.x, sc.shootingPoint.y, sc.shootingDirection, 200, 20, weapon.getDamage(), model.getWorld(), Masks.PLAYER_BULLET);
        getEngine().add(bullet);
    }



    @Override
    public void update(float dt) {
        ComponentMapper<ShooterComponent> shooterM = Mappers.shooterM;
        for (Entity entity : shooters) {
            ShooterComponent sc = entity.get(shooterM);
            updateShootingPoint(entity, sc);
            sc.shootingDirection.setAngle(entity.getAngle());
        }

    }

    public void updateShootingPoint(@NotNull Entity entity, @NotNull ShooterComponent sc){
        sc.shootingPoint.set(sc.relativeX, sc.relativeY).rotate(entity.getAngle()).add(entity.x, entity.y);
    }

    @Override
    public void removeFromEngine() {
        getEngine().unsubscribe(ShootEvent.class, listener);
    }
}
