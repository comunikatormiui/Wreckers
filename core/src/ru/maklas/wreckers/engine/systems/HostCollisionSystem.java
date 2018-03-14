package ru.maklas.wreckers.engine.systems;

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
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.client.entities.EntityString;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.components.StatusEffectComponent;
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.components.WreckerComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;
import ru.maklas.wreckers.engine.others.DisarmStatusEffect;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;

import static ru.maklas.wreckers.assets.EntityType.*;
import static ru.maklas.wreckers.assets.GameAssets.leagueFormula;

/**
 * <p>
 * Система принимает CollisionEvent'ы. и обрабатывает, генерируя:
 * <li>DetachRequest</li>
 * <li>HitEvent</li>
 * <p>
 * Отвечает за коллизию тел, разделяя на коллизию:
 * <li>Игрока с оружием</li>
 * <li>Игрока с игроком</li>
 * <li>Оружия с оружием</li>
 *
 * Не предпринимает никаких действий с Entity. Только диспатчит
 */
public class HostCollisionSystem extends EntitySystem{

    private final GameModel model;

    public HostCollisionSystem(GameModel model) {
        this.model = model;
    }

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

    // Контакт и импульс нельзя передавать далее
    private void handleWeaponToWeapon(final Entity weaponA, EntityType typeA, Entity weaponB, EntityType typeB, Contact contact, ContactImpulse impulse){
        final float impulseAdjustment = 0.12f;
        float impulseForce = impulse.getNormalImpulses()[0] * impulseAdjustment;
        PickUpComponent w1Pick = weaponA.get(Mappers.pickUpM);
        PickUpComponent w2Pick = weaponB.get(Mappers.pickUpM);
        if (w1Pick == null || w2Pick == null) {
            return;
        }

        @Nullable Entity owner1 = w1Pick.owner;
        @Nullable Entity owner2 = w1Pick.owner;
        @Nullable WreckerComponent wr1 = owner1 == null ? null : owner1.get(Mappers.wreckerM);
        @Nullable WreckerComponent wr2 = owner2 == null ? null : owner2.get(Mappers.wreckerM);
        @Nullable WeaponComponent wc1 = owner1 == null ? null : owner1.get(Mappers.weaponM);
        @Nullable WeaponComponent wc2 = owner2 == null ? null : owner2.get(Mappers.weaponM);
        float disarmResist1 = wr1 == null ? 9999999f : wr1.disarmResist;
        float disarmResist2 = wr2 == null ? 9999999f : wr2.disarmResist;

        final float minToDisarm = 50;
        boolean disarm1 = impulseForce * (wc2 == null ? 1 : wc2.disarmAbility) * leagueFormula(disarmResist1) > minToDisarm;
        boolean disarm2 = impulseForce * (wc1 == null ? 1 : wc1.disarmAbility) * leagueFormula(disarmResist2) > minToDisarm;
        if (impulseForce * leagueFormula(disarmResist2) > 1) System.out.println(impulseForce * leagueFormula(disarmResist1) * (wc2 == null ? 1 : wc2.disarmAbility) + " : " + impulseForce * leagueFormula(disarmResist2) * (wc1 == null ? 1 : wc1.disarmAbility));

        if (disarm1) {
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponA));
            if (owner1 != null){
                getEngine().add(new EntityString("Disarm!", 1, new Vector2(contact.getWorldManifold().getPoints()[0]).scl(GameAssets.box2dScale).x, new Vector2(contact.getWorldManifold().getPoints()[0]).scl(GameAssets.box2dScale).y, Color.ORANGE));
                StatusEffectComponent sec = owner1.get(Mappers.effectM);
                if (sec != null){
                    getEngine().add(new EntityString("Disarm!", 1, new Vector2(contact.getWorldManifold().getPoints()[0]).scl(GameAssets.box2dScale).x, new Vector2(contact.getWorldManifold().getPoints()[0]).scl(GameAssets.box2dScale).y + 25, Color.ORANGE));
                    sec.add(new DisarmStatusEffect(4));
                }
            }
        }
        if (disarm2) {
            getEngine().dispatchLater(new DetachRequest(null, DetachRequest.Type.TARGET_WEAPON, weaponB));
            if (owner2 != null) {
                StatusEffectComponent sec = owner1.get(Mappers.effectM);
                if (sec != null) {
                    sec.add(new DisarmStatusEffect(4));
                }
            }
        }
    }

    // Контакт и импульс нельзя передавать далее
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


        final Vector2 playerNormal = calculatePlayerNormal(manifold, weaponIsA, playerFixture, collisionPoint); //Вектор нормали игрока в точке
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
        float angle = worldPierceDirection.angle(collisionVelocity); // будут ценится значения ~0 и ~180
        angle = angle < 0 ? -angle : angle; // берем модуль 0..180
        angle -= 90; // -90..90 Ценятся ~-90 и ~90
        angle = angle < 0 ? -angle : angle; // снова берем модуль 0..90. где 90 - топ
        angle /= 90f; //0..1
        return angle > 0.6f ? angle : 0; //Отсеиваем тупые удары
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

    // Контакт и импульс нельзя передавать далее
    private void handlePlayerToPlayer(Entity playerA, EntityType typeA, Entity playerB, EntityType typeB, Contact contact, ContactImpulse impulse){

    }

}
