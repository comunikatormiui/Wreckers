package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.entities.EntityArrow;
import ru.maklas.wreckers.client.entities.EntityNumber;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.DamageType;
import ru.maklas.wreckers.engine.events.damage.DamageData;
import ru.maklas.wreckers.engine.events.damage.WeaponHitDamageData;
import ru.maklas.wreckers.engine.events.requests.DamageRequest;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.game.FixtureData;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.libs.Utils;

import static ru.maklas.wreckers.assets.EntityType.*;

public class CollisionSystem extends EntitySystem{

    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(new Subscription<CollisionEvent>(CollisionEvent.class) {
            @Override
            public void receive(Signal<CollisionEvent> signal, CollisionEvent e) {
                EntityType typeA = fromType(e.getA().type);
                EntityType typeB = fromType(e.getB().type);
                if (e.getImpulse().getCount() == 0){
                    return;
                }

                if (isPlayerOrOpponent(typeA) && isPlayerOrOpponent(typeB)){
                    handlePlayerToPlayer(e.getA(), typeA, e.getB(), typeB, e.getContact(), e.getImpulse());

                } else if (isWeapon(typeA) && isWeapon(typeB)){
                    handleWeaponToWeapon(e.getA(), typeA, e.getB(), typeB, e.getContact(), e.getImpulse());

                } else if (isPlayerOrOpponent(typeA) && isWeapon(typeB)){
                    handleWeaponToPlayer(e.getB(), typeB, e.getA(), typeA, e.getContact(), e.getImpulse(), false);

                } else if (isPlayerOrOpponent(typeB) && isWeapon(typeA)){
                    handleWeaponToPlayer(e.getA(), typeA, e.getB(), typeB, e.getContact(), e.getImpulse(), true);
                }
            }
        });
    }

    private void handleWeaponToWeapon(Entity weaponA, EntityType typeA, Entity weaponB, EntityType typeB, Contact contact, ContactImpulse impulse){
        float impulseForce = impulse.getNormalImpulses()[0];
        if (impulseForce > 100){
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponA));
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponB));
        }
    }

    private void handleWeaponToPlayer(final Entity weapon, EntityType weaponType, final Entity player, EntityType playerType, Contact contact, ContactImpulse impulse, boolean weaponIsA){
        final float minimumImpulse = 100;
        float impulseForce = impulse.getNormalImpulses()[0];
        if (impulseForce < minimumImpulse){
            return;
        }

        WeaponComponent wc = weapon.get(Mappers.weaponM);
        if (wc == null){
            return;
        }

        final WorldManifold worldManifold = contact.getWorldManifold();
        final Vector2 normal = worldManifold.getNormal();
        final Vector2 point = worldManifold.getPoints()[0];
        final Fixture weaponFixture = weaponIsA ? contact.getFixtureA() : contact.getFixtureB();
        final Fixture playerFixture = weaponIsA ? contact.getFixtureB() : contact.getFixtureA();
        final Body playerBody = playerFixture.getBody();
        PickUpComponent pickUpC = weapon.get(Mappers.pickUpM);
        @Nullable final Entity weaponOwner = pickUpC == null ? null : pickUpC.owner;


        { //Вектор удара на экран
            Utils.vec1.set(point).scl(GameAssets.box2dScale);
            Utils.vec2.set(normal).scl(GameAssets.box2dScale).scl(5).add(Utils.vec1);
            getEngine().add(new EntityArrow(Utils.vec1, Utils.vec2, 1, Color.BLUE));
        }

        FixtureType weaponFixtureType = ((FixtureData) weaponFixture.getUserData()).getFixtureType();
        Vector2 collisionPoint = Utils.vec1.set(point).scl(GameAssets.box2dScale);

        if (weaponFixtureType == FixtureType.WEAPON_DAMAGE) { // если ударившая часть является дамажущей
            final float damageGenerated = wc.impulseDamageMultiplier * (impulseForce / 100); // Рассчитываем урон
            getEngine().add(new EntityNumber((int) damageGenerated, 2, collisionPoint.x, collisionPoint.y));
            System.out.println("Damage dealt to" + player + ": " + damageGenerated);
            final Vector2 force = new Vector2(normal).scl(playerBody.getMass() * wc.additionalPush); // Сила отталкивания
            DamageData damageData = new WeaponHitDamageData(player, damageGenerated, weapon, weaponOwner, new Vector2(point).scl(GameAssets.box2dScale), new Vector2(normal));
            getEngine().dispatchLater(new DamageRequest(damageData));
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    playerFixture.getBody().applyForce(force, point, true);
                }
            });
            System.out.println("Applying force of  " + force.len());
        }

    }

    private void handlePlayerToPlayer(Entity playerA, EntityType typeA, Entity playerB, EntityType typeB, Contact contact, ContactImpulse impulse){

    }

}
