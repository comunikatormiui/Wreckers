package ru.maklas.wreckers.engine.systems;

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
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;

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

    //  онтакт нельз€ передавать далее
    private void handleWeaponToWeapon(final Entity weaponA, EntityType typeA, Entity weaponB, EntityType typeB, Contact contact, ContactImpulse impulse){
        float impulseForce = impulse.getNormalImpulses()[0];
        if (impulseForce > 250){
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponA));
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponB));
        }
    }

    //  онтакт нельз€ передавать далее
    private void handleWeaponToPlayer(final Entity weapon, EntityType weaponType, final Entity player, EntityType playerType, Contact contact, ContactImpulse impulse, boolean weaponIsA){
        //*********************//
        //* FIXTURE CONDITION *//
        //*********************//
        final Fixture weaponFixture = weaponIsA ? contact.getFixtureA() : contact.getFixtureB();
        final FixtureData weaponFixtureData = (FixtureData) weaponFixture.getUserData();
        if (weaponFixtureData.getFixtureType() != FixtureType.WEAPON_DAMAGE) {
            return;
        }


        //******************************//
        //* MINIMAL VELOCITY CONDITION *//
        //******************************//
        final Fixture playerFixture = weaponIsA ? contact.getFixtureB() : contact.getFixtureA();
        final Body weaponBody = weaponFixture.getBody();
        final Body playerBody = playerFixture.getBody();
        final WorldManifold manifold = contact.getWorldManifold();
        final Vector2 collisionPoint = manifold.getPoints()[0];
        final Vector2 collisionVelocity = new Vector2(weaponBody.getLinearVelocityFromWorldPoint(collisionPoint)).sub(playerBody.getLinearVelocityFromWorldPoint(collisionPoint));
        if (collisionVelocity.len2() < 15){
            return;
        }
        System.out.println(collisionVelocity.len2());


        //**********************//
        //* DULL, SLICE, SHARP *//
        //**********************//
        float dullness;
        float sliceness;
        float sharpness;


        final Vector2 playerNormal = calculatePlayerNormal(manifold, weaponIsA, playerFixture, collisionPoint); //¬ектор нормали игрока в точке
        final Vector2 piercingDirection = new Vector2(1, 0);
        final Vector2 weaponStuckPoint = new Vector2(1, 0);
        dullness = calculateDullness(collisionVelocity, playerNormal);
        sliceness = 1 - dullness;
        if (weaponFixtureData instanceof WeaponPiercingFD) {
            sharpness = calculateSharpness((WeaponPiercingFD) weaponFixtureData, weaponFixture, collisionVelocity);
            float oldDullness = dullness;
            dullness = oldDullness * (1 - sharpness);
            sharpness = oldDullness * sharpness;
            ((WeaponPiercingFD) weaponFixtureData).getWorldDirection(weaponFixture, piercingDirection);
            weaponStuckPoint.set(((WeaponPiercingFD) weaponFixtureData).getStuckPoint());
        } else {
            sharpness = 0;
        }

        //************//
        //* DISPATCH *//
        //************//
        final float impulseForce = impulse.getNormalImpulses()[0];
        PickUpComponent pickUpC = weapon.get(Mappers.pickUpM);
        @Nullable final Entity weaponOwner = pickUpC == null ? null : pickUpC.owner;
        WeaponWreckerHitEvent event = new WeaponWreckerHitEvent(
                weapon,
                weaponOwner,
                player,
                new Vector2(collisionPoint).scl(GameAssets.box2dScale),
                new Vector2(playerNormal),
                collisionVelocity,
                piercingDirection,
                weaponStuckPoint,
                impulseForce,

                sliceness,
                dullness,
                sharpness,

                weaponBody,
                playerBody);
        getEngine().dispatchLater(event);
    }

    private float calculateSharpness(WeaponPiercingFD weaponFD, Fixture weaponFixture, Vector2 collisionVelocity) {
        Vector2 worldPierceDirection = new Vector2();
        weaponFD.getWorldDirection(weaponFixture, worldPierceDirection);
        float angle = worldPierceDirection.angle(collisionVelocity); // будут ценитс€ значени€ ~0 и ~180
        angle = angle < 0 ? -angle : angle; // берем модуль 0..180
        angle -= 90; // -90..90 ÷ен€тс€ ~-90 и ~90
        angle = angle < 0 ? -angle : angle; // снова берем модуль 0..90. где 90 - топ
        return angle / 90f;
    }

    private Vector2 calculatePlayerNormal(WorldManifold manifold, boolean weaponIsA, Fixture playerFixture, Vector2 collisionPoint) {
        if (playerFixture.getShape() instanceof CircleShape){

            return new Vector2(collisionPoint).sub(playerFixture.getBody().getPosition()).nor();
        } else {
            return new Vector2(weaponIsA ? manifold.getNormal().scl(-1) : manifold.getNormal());
        }
    }

    public static float calculateDullness(Vector2 collisionVelocity, Vector2 box2dPlayerNormal){
        float angle = collisionVelocity.angle(box2dPlayerNormal);
        angle = angle < 0 ? -angle : angle; // abs(angle) 0..180
        angle -= 90; // -90..90
        angle = angle < 0 ? -angle : angle; //0..90 где 0 - абсолютно режущий удар, а 90 - абсолютно тупой
        return angle / 90f;
    }

    //  онтакт нельз€ передавать далее
    private void handlePlayerToPlayer(Entity playerA, EntityType typeA, Entity playerB, EntityType typeB, Contact contact, ContactImpulse impulse){

    }

}
