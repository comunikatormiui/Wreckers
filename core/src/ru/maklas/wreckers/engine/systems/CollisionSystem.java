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
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;
import ru.maklas.wreckers.game.FixtureData;
import ru.maklas.wreckers.game.FixtureType;

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

    // Контакт нельзя передавать далее
    private void handleWeaponToWeapon(final Entity weaponA, EntityType typeA, Entity weaponB, EntityType typeB, Contact contact, ContactImpulse impulse){
        float impulseForce = impulse.getNormalImpulses()[0];
        if (impulseForce > 100){
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponA));
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponB));
        }
    }

    // Контакт нельзя передавать далее
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

        final WorldManifold manifold = contact.getWorldManifold();
        final Fixture weaponFixture = weaponIsA ? contact.getFixtureA() : contact.getFixtureB();
        final Body weaponBody = weaponFixture.getBody();
        final Fixture playerFixture = weaponIsA ? contact.getFixtureB() : contact.getFixtureA();
        final Body playerBody = playerFixture.getBody();

        final Vector2 collisionPoint = manifold.getPoints()[0];
        final Vector2 playerNormal = calculatePlayerNormal(manifold, weaponIsA, playerFixture, collisionPoint); //Вектор нормали игрока в точке

        PickUpComponent pickUpC = weapon.get(Mappers.pickUpM);
        @Nullable final Entity weaponOwner = pickUpC == null ? null : pickUpC.owner;


        // скорость удара в точке
        Vector2 collisionPointVelocity = new Vector2(weaponBody.getLinearVelocityFromWorldPoint(collisionPoint)).sub(playerBody.getLinearVelocityFromWorldPoint(collisionPoint));
        float dullPercent = calculateDullness(collisionPointVelocity, playerNormal); // на сколько процентов данный удар является прямым
        float sharpPercent = 1 - dullPercent; // на сколько процентов данный удар является режущим

        FixtureType weaponFixtureType = ((FixtureData) weaponFixture.getUserData()).getFixtureType();

        if (weaponFixtureType == FixtureType.WEAPON_DAMAGE) { // если ударившая часть является дамажущей
            WeaponWreckerHitEvent event = new WeaponWreckerHitEvent(weapon, weaponOwner, player, new Vector2(collisionPoint).scl(GameAssets.box2dScale), new Vector2(playerNormal), collisionPointVelocity, impulseForce, sharpPercent, dullPercent, weaponBody, playerBody);
            getEngine().dispatchLater(event);
        }

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

    // Контакт нельзя передавать далее
    private void handlePlayerToPlayer(Entity playerA, EntityType typeA, Entity playerB, EntityType typeB, Contact contact, ContactImpulse impulse){

    }

}
