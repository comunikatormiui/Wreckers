package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.World;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.entities.ClientEntityPistolBullet;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PlayerComponent;
import ru.maklas.wreckers.engine.components.ShooterComponent;
import ru.maklas.wreckers.engine.events.WeaponChangeEvent;
import ru.maklas.wreckers.engine.events.requests.ShootRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponChangeRequest;
import ru.maklas.wreckers.game.Weapon;
import ru.maklas.wreckers.game.WeaponType;

public class ClientShooterSystem extends EntitySystem {

    private final World world;
    private ImmutableArray<Entity> shooters;
    private ImmutableArray<Entity> players;
    private Listener<ShootRequest> listener;
    private Listener<WeaponChangeRequest> listener2;

    public ClientShooterSystem(World world) {
        this.world = world;
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        shooters = engine.entitiesFor(ShooterComponent.class);
        players = engine.entitiesFor(PlayerComponent.class);
        listener = new Listener<ShootRequest>() {
            @Override
            public void receive(Signal<ShootRequest> signal, ShootRequest shootEvent) {
                Entity shooter = shootEvent.getShooter();
                ShooterComponent sc = shooter.get(Mappers.shooterM);
                if (sc == null) {
                    return;
                }
                PlayerComponent bc = shooter.get(Mappers.playerM);
                if (bc == null) {
                    return;
                }

                shoot(shooter, sc, bc);
            }
        };
        listener2 = new Listener<WeaponChangeRequest>() {
            @Override
            public void receive(Signal<WeaponChangeRequest> signal, WeaponChangeRequest weaponChangeRequest) {
                PlayerComponent inv = weaponChangeRequest.getEntity().get(Mappers.playerM);
                if (inv == null){
                    return;
                }

                Weapon chosen = null;
                for (Weapon weapon : inv.bag.weapons) {
                    if (weapon.type == weaponChangeRequest.getWeaponType()){
                        chosen = weapon;
                        break;
                    }
                }

                if (chosen != null){
                    Weapon oldWeapon = inv.currentWeapon;
                    inv.currentWeapon = chosen;
                    engine.dispatch(new WeaponChangeEvent(weaponChangeRequest.getEntity(), oldWeapon, chosen));
                }

            }
        };
        engine.subscribe(ShootRequest.class, listener);
        engine.subscribe(WeaponChangeRequest.class, listener2);
    }

    private void shoot(@NotNull Entity shooter, @NotNull ShooterComponent sc, @NotNull PlayerComponent bc) {
        if (shooter.type == EntityType.PLAYER.type){
            shootAsPlayer(shooter, sc, bc);
        }
    }

    private void shootAsPlayer(Entity shooter, ShooterComponent sc, PlayerComponent bc) {
        if (bc.currentWeapon.type == WeaponType.PISTOL){
            shootPlayerPistol(shooter, sc, bc.currentWeapon);
        }
    }

    private void shootPlayerPistol(Entity shooter, ShooterComponent sc, Weapon weapon) {
        if (weapon.isOnCooldown()){
            return;
        }
        if (!weapon.canDec(1)){
            return;
        }

        weapon.decAmmo(1);
        weapon.setCooldownToMax();
        ClientEntityPistolBullet bullet = new ClientEntityPistolBullet(shooter, sc.shootingPoint.x, sc.shootingPoint.y, sc.shootingDirection, weapon.getRange(), weapon.getForce(), weapon.getDamage(), world, EntityType.PLAYER_BULLET);
        getEngine().add(bullet);
    }



    @Override
    public void update(float dt) {
        ComponentMapper<ShooterComponent> shooterM = Mappers.shooterM;
        ComponentMapper<PlayerComponent> inventoryM = Mappers.playerM;
        for (Entity entity : shooters) {
            ShooterComponent sc = entity.get(shooterM);
            updateShootingPoint(entity, sc);
            sc.shootingDirection.setAngle(entity.getAngle());
        }

        for (Entity entity : players) {
            Weapon currentWeapon = entity.get(inventoryM).currentWeapon;
            if (currentWeapon.isOnCooldown()){
                currentWeapon.decCurrentCooldown(dt);
            }
        }

    }

    public void updateShootingPoint(@NotNull Entity entity, @NotNull ShooterComponent sc){
        sc.shootingPoint.set(sc.relativeX, sc.relativeY).rotate(entity.getAngle()).add(entity.x, entity.y);
    }

    @Override
    public void removeFromEngine() {
        getEngine().unsubscribe(ShootRequest.class, listener);
        getEngine().unsubscribe(WeaponChangeRequest.class, listener2);
    }
}
