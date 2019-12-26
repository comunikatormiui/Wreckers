package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.SubscriptionSystem;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.weapon.PickUpComponent;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.weapon.WeaponWreckerHitEvent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.BiConsumer;
import ru.maklas.wreckers.utils.Log;

import static ru.maklas.wreckers.statics.EntityType.isPlayerOrOpponent;
import static ru.maklas.wreckers.statics.EntityType.isWeapon;
import static ru.maklas.wreckers.statics.Game.leagueFormula;

/** Resolves collision of Entities (Wreckers, Weapons, projectiles, etc...) **/
public class CollisionSystem extends SubscriptionSystem {

	@Override
	public void onAddedToEngine(final Engine engine) {
		subscribe(PostCollisionEvent.class, this::onPostCollisionEvent);
	}

	protected void onPostCollisionEvent(PostCollisionEvent e) {
		int typeA = e.getA().type;
		int typeB = e.getB().type;

		if (isPlayerOrOpponent(typeA) && isPlayerOrOpponent(typeB)) {
			handlePlayerToPlayer(e.getA(), e.getB());

		} else if (isWeapon(typeA) && isWeapon(typeB)) {
			handleWeaponToWeapon(e.getA(), e.getB(), e.getPoint(), e.getNormalImpulse());

		} else if (isPlayerOrOpponent(typeA) && isWeapon(typeB)) {
			handleWeaponToPlayer(e, false);

		} else if (isPlayerOrOpponent(typeB) && isWeapon(typeA)) {
			handleWeaponToPlayer(e, true);
		}
	}

	protected void handleWeaponToPlayer(PostCollisionEvent e, boolean weaponIsA) {

	}

	protected void handleWeaponToWeapon(Entity a, Entity b, Vector2 point, float impulse) {

	}

	protected void handlePlayerToPlayer(Entity a, Entity b) {

	}

	/**
	 * @param weaponOwnerConsumer - accept weapon and owner of the weapon that must be disarmed.
	 */
	protected final void determineDisarm(Entity weaponA, Entity weaponB, Vector2 point, float impulse, BiConsumer<Entity, Entity> weaponOwnerConsumer) {
		final float impulseAdjustment = 0.12f;
		float impulseForce = impulse * impulseAdjustment;
		PickUpComponent w1Pick = weaponA.get(M.pickUp);
		PickUpComponent w2Pick = weaponB.get(M.pickUp);
		if (w1Pick == null || w2Pick == null) return; //Weapons can't be picked up, so no disarm

		@Nullable Entity owner1 = w1Pick.owner;
		@Nullable Entity owner2 = w2Pick.owner;
		if (owner1 == null && owner2 == null) return; //Both lack owners

		@Nullable WreckerComponent wr1 = owner1 == null ? null : owner1.get(M.wrecker);
		@Nullable WreckerComponent wr2 = owner2 == null ? null : owner2.get(M.wrecker);
		@Nullable WeaponComponent wc1 = owner1 == null ? null : owner1.get(M.weapon);
		@Nullable WeaponComponent wc2 = owner2 == null ? null : owner2.get(M.weapon);
		float disarmResist1 = wr1 == null ? 9999999f : wr1.disarmResist;
		float disarmResist2 = wr2 == null ? 9999999f : wr2.disarmResist;

		final float minToDisarm = 50;
		boolean disarm1 = impulseForce * (wc2 == null ? 1 : wc2.disarmAbility) * leagueFormula(disarmResist1) > minToDisarm;
		boolean disarm2 = impulseForce * (wc1 == null ? 1 : wc1.disarmAbility) * leagueFormula(disarmResist2) > minToDisarm;

		if (impulseForce * leagueFormula(disarmResist2) > 1) { //Log disarm values
			float disarmAValue = impulseForce * leagueFormula(disarmResist1) * (wc2 == null ? 1 : wc2.disarmAbility);
			float disarmBValue = impulseForce * leagueFormula(disarmResist2) * (wc1 == null ? 1 : wc1.disarmAbility);
			Log.debug("Disarm stats: " + disarmAValue + " : " + disarmBValue);
		}

		if (disarm1) {
			weaponOwnerConsumer.accept(owner1, weaponA);
		}
		if (disarm2) {
			weaponOwnerConsumer.accept(owner2, weaponB);
		}
	}

	/**
	 * If succedes, dispatches {@link WeaponWreckerHitEvent}
	 * @param e A must be weapon, B must be player
	 */
	public static void doWeaponToPlayerHit(PostCollisionEvent e, Engine engine){
		//*********************//
		//* FIXTURE CONDITION *//
		//*********************//
		boolean weaponIsA = true;
		final Fixture weaponFixture = e.getFixA();
		final FixtureData weaponFixtureData = (FixtureData) weaponFixture.getUserData();
		if (weaponFixtureData.getFixtureType() != FixtureType.WEAPON_DAMAGE) return;



		//******************************//
		//* MINIMAL VELOCITY CONDITION *//
		//******************************//
		final Entity weapon = e.getA();
		final Entity player = e.getB();
		final Fixture playerFixture = e.getFixB();
		final Body weaponBody = weaponFixture.getBody();
		final Body playerBody = playerFixture.getBody();
		final Vector2 worldCollisionPoint = new Vector2(e.getPoint()).scl(Game.scaleReversed);
		final Vector2 collisionVelocity = new Vector2(weaponBody.getLinearVelocityFromWorldPoint(worldCollisionPoint)).sub(playerBody.getLinearVelocityFromWorldPoint(worldCollisionPoint));


		//**********************//
		//* DULL, SLICE, SHARP *//
		//**********************//
		float dullness;
		float sliceness;
		float sharpness;


		final Vector2 playerNormal = calculatePlayerNormal(playerFixture, worldCollisionPoint, e.getNormal(), weaponIsA); //Вектор нормали игрока в точке
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
		PickUpComponent pickUpC = weapon.get(M.pickUp);
		@Nullable final Entity weaponOwner = pickUpC == null ? null : pickUpC.owner;
		WeaponWreckerHitEvent event = new WeaponWreckerHitEvent(
				weapon,
				weaponOwner,
				player,
				new Vector2(worldCollisionPoint).scl(Game.scale),
				new Vector2(playerNormal),
				collisionVelocity,
				piercingDirection,
				weaponStuckPoint,
				e.getNormalImpulse(),

				sliceness,
				dullness,
				sharpness,

				weaponBody,
				playerBody);
		engine.dispatch(event);
	}

	/**
	 * @param collisionVelocity Velocity of collision of Wrecker and Weapon
	 * @param box2dPlayerNormal
	 * @return 0...1 where 1 means dull and 0 is slicing
	 */
	public static float calculateDullness(Vector2 collisionVelocity, Vector2 box2dPlayerNormal) {
		float angle = collisionVelocity.angle(box2dPlayerNormal);
		angle = angle < 0 ? -angle : angle; // abs(angle) 0..180
		angle -= 90; // -90..90
		angle = angle < 0 ? -angle : angle; //0..90 где 0 - абсолютно режущий удар, а 90 - абсолютно тупой
		return angle / 90f;
	}


	/**
	 * @param playerFixture Игрок
	 * @param collisionPoint точка прикосновения
	 * @param normal Нормаль прикосновения
	 * @param weaponIsA Оружие - А в изначальном контакте
	 * @return Вектор нормали от игрока к точке
	 */
	public static Vector2 calculatePlayerNormal(Fixture playerFixture, Vector2 collisionPoint, Vector2 normal, boolean weaponIsA) {
		if (playerFixture.getShape() instanceof CircleShape) { //Если игрок - круг, то вектор нормали всегда исходит от центра к точке.
			return new Vector2(collisionPoint).sub(playerFixture.getBody().getPosition()).nor();
		} else {
			Vector2 n = new Vector2(normal);
			return weaponIsA ? n.scl(-1) : n;
		}
	}

	/**
	 * @param weaponFD Тип Fixture оружия - проникающий
	 * @param weaponFixture сама Fixture
	 * @return 0..1 Sharpness
	 */
	public static float calculateSharpness(@NotNull WeaponPiercingFD weaponFD, Fixture weaponFixture, Vector2 collisionVelocity) {
		Vector2 worldPierceDirection = new Vector2();
		weaponFD.getWorldDirection(weaponFixture, worldPierceDirection);
		float angle = worldPierceDirection.angle(collisionVelocity); // будут ценится значения ~0 и ~180
		angle = angle < 0 ? -angle : angle; // берем модуль 0..180
		angle -= 90; // -90..90 Ценятся ~-90 и ~90
		angle = angle < 0 ? -angle : angle; // снова берем модуль 0..90. где 90 - топ
		angle /= 90f; //0..1
		return angle > 0.6f ? angle : 0; //Отсеиваем тупые удары
	}

}
