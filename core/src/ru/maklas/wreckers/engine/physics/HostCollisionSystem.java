package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.status_effects.DisarmStatusEffect;
import ru.maklas.wreckers.engine.status_effects.StatusEffectComponent;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.engine.weapon.PickUpComponent;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.weapon.WeaponWreckerHitEvent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.entities.EntityString;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;
import ru.maklas.wreckers.statics.Game;

import static ru.maklas.wreckers.statics.EntityType.isPlayerOrOpponent;
import static ru.maklas.wreckers.statics.EntityType.isWeapon;
import static ru.maklas.wreckers.statics.Game.leagueFormula;

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
 * <p>
 * Не предпринимает никаких действий с Entity. Только диспатчит
 */
public class HostCollisionSystem extends EntitySystem {

	@Override
	public void onAddedToEngine(final Engine engine) {
		subscribe(PostCollisionEvent.class, this::onPostCollisionEvent);
	}

	@Override
	public void update(float dt) {

	}

	// Контакт и импульс нельзя передавать далее
	private void handleWeaponToWeapon(final Entity weaponA, int typeA, Entity weaponB, int typeB, Vector2 point, float impulse) {
		final float impulseAdjustment = 0.12f;
		float impulseForce = impulse * impulseAdjustment;
		PickUpComponent w1Pick = weaponA.get(M.pickUp);
		PickUpComponent w2Pick = weaponB.get(M.pickUp);
		if (w1Pick == null || w2Pick == null) {
			return;
		}

		@Nullable Entity owner1 = w1Pick.owner;
		@Nullable Entity owner2 = w1Pick.owner;
		@Nullable WreckerComponent wr1 = owner1 == null ? null : owner1.get(M.wrecker);
		@Nullable WreckerComponent wr2 = owner2 == null ? null : owner2.get(M.wrecker);
		@Nullable WeaponComponent wc1 = owner1 == null ? null : owner1.get(M.weapon);
		@Nullable WeaponComponent wc2 = owner2 == null ? null : owner2.get(M.weapon);
		float disarmResist1 = wr1 == null ? 9999999f : wr1.disarmResist;
		float disarmResist2 = wr2 == null ? 9999999f : wr2.disarmResist;

		final float minToDisarm = 50;
		boolean disarm1 = impulseForce * (wc2 == null ? 1 : wc2.disarmAbility) * leagueFormula(disarmResist1) > minToDisarm;
		boolean disarm2 = impulseForce * (wc1 == null ? 1 : wc1.disarmAbility) * leagueFormula(disarmResist2) > minToDisarm;
		if (impulseForce * leagueFormula(disarmResist2) > 1)
			System.out.println(impulseForce * leagueFormula(disarmResist1) * (wc2 == null ? 1 : wc2.disarmAbility) + " : " + impulseForce * leagueFormula(disarmResist2) * (wc1 == null ? 1 : wc1.disarmAbility));

		if (disarm1) {
			getEngine().dispatchLater(new DetachRequest(DetachRequest.Type.TARGET_WEAPON, null, weaponA));
			if (owner1 != null) {
				getEngine().add(new EntityString("Disarm!", 1, point.x, point.y, Color.ORANGE));
				StatusEffectComponent sec = owner1.get(M.effect);
				if (sec != null) {
					getEngine().add(new EntityString("Disarm!", 1, point.x, point.y + 25, Color.ORANGE));
					sec.add(new DisarmStatusEffect(4));
				}
			}
		}
		if (disarm2) {
			getEngine().dispatchLater(new DetachRequest(DetachRequest.Type.TARGET_WEAPON, null, weaponB));
			if (owner2 != null) {
				StatusEffectComponent sec = owner1.get(M.effect);
				if (sec != null) {
					sec.add(new DisarmStatusEffect(4));
				}
			}
		}
	}

	// Контакт и импульс нельзя передавать далее
	private void handleWeaponToPlayer(PostCollisionEvent e, boolean weaponIsA) {
		//*********************//
		//* FIXTURE CONDITION *//
		//*********************//
		final Fixture weaponFixture = weaponIsA ? e.getFixA() : e.getFixB();
		final FixtureData weaponFixtureData = (FixtureData) weaponFixture.getUserData();
		if (weaponFixtureData.getFixtureType() != FixtureType.WEAPON_DAMAGE) {
			return;
		}

		Entity weapon = weaponIsA ? e.getA() : e.getB();
		Entity player = weaponIsA ? e.getB() : e.getA();


		//******************************//
		//* MINIMAL VELOCITY CONDITION *//
		//******************************//
		final Fixture playerFixture = weaponIsA ? e.getFixB() : e.getFixB();
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


		final Vector2 playerNormal = calculatePlayerNormal(e.getNormal(), weaponIsA, playerFixture, worldCollisionPoint); //Вектор нормали игрока в точке
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

	private Vector2 calculatePlayerNormal(Vector2 normal, boolean weaponIsA, Fixture playerFixture, Vector2 collisionPoint) {
		if (playerFixture.getShape() instanceof CircleShape) {

			return new Vector2(collisionPoint).sub(playerFixture.getBody().getPosition()).nor();
		} else {
			Vector2 n = new Vector2(normal);
			return weaponIsA ? n.scl(-1) : n;
		}
	}

	public static float calculateDullness(Vector2 collisionVelocity, Vector2 box2dPlayerNormal) {
		float angle = collisionVelocity.angle(box2dPlayerNormal);
		angle = angle < 0 ? -angle : angle; // abs(angle) 0..180
		angle -= 90; // -90..90
		angle = angle < 0 ? -angle : angle; //0..90 где 0 - абсолютно режущий удар, а 90 - абсолютно тупой
		return angle / 90f;
	}

	// Контакт и импульс нельзя передавать далее
	private void handlePlayerToPlayer(Entity playerA, int typeA, Entity playerB, int typeB) {

	}

	private void onPostCollisionEvent(PostCollisionEvent e) {
		int typeA = e.getA().type;
		int typeB = e.getB().type;

		if (isPlayerOrOpponent(typeA) && isPlayerOrOpponent(typeB)) {
			handlePlayerToPlayer(e.getA(), typeA, e.getB(), typeB);

		} else if (isWeapon(typeA) && isWeapon(typeB)) {
			handleWeaponToWeapon(e.getA(), typeA, e.getB(), typeB, e.getPoint(), e.getNormalImpulse());

		} else if (isPlayerOrOpponent(typeA) && isWeapon(typeB)) {
			handleWeaponToPlayer(e, false);

		} else if (isPlayerOrOpponent(typeB) && isWeapon(typeA)) {
			handleWeaponToPlayer(e, true);
		}
	}
}
