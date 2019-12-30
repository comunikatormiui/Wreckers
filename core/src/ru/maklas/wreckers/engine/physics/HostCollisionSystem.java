package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.LongMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.SubscriptionSystem;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.other.FrameTrackSystem;
import ru.maklas.wreckers.engine.status_effects.DisarmStatusEffect;
import ru.maklas.wreckers.engine.status_effects.StatusEffectComponent;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.engine.weapon.PickUpComponent;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.entities.EntityString;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.BiConsumer;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.StringUtils;

import static ru.maklas.wreckers.statics.Game.leagueFormula;

/** Resolves collision of Entities (Wreckers, Weapons, projectiles, etc...) **/
public class HostCollisionSystem extends CollisionSystem {

	/** Stores event in preSolve, adds impulse in postSolve and dispatches **/
	private LongMap<WeaponWreckerHitEvent> weaponWreckerFrameEventMap;

	@Override
	public void onAddedToEngine(final Engine engine) {
		weaponWreckerFrameEventMap = new LongMap<>();
		if (engine.getSystemManager().getSystem(FrameTrackSystem.class) == null) {
			throw new RuntimeException("Add " + FrameTrackSystem.class.getSimpleName() + " before " + getClass().getSimpleName());
		}
		subscribe(FrameTrackSystem.class, e -> weaponWreckerFrameEventMap.clear());
		subscribe(PreSolveEvent.class, p -> preSolve(p.contact, p.oldManifold));
		subscribe(PostSolveEvent.class, p -> postSolve(p.getContact(), p.getImpulse()));
	}

	private void preSolve(Contact contact, Manifold oldManifold) {
		FixtureData fDataA = (FixtureData) contact.getFixtureA().getUserData();
		FixtureData fDataB = (FixtureData) contact.getFixtureB().getUserData();
		boolean wreckerIsA;
		if (fDataA.getFixtureType() == FixtureType.WRECKER_BODY && fDataB.getFixtureType() == FixtureType.WEAPON_DAMAGE) {
			wreckerIsA = true;
		} else if (fDataA.getFixtureType() == FixtureType.WEAPON_DAMAGE && fDataB.getFixtureType() == FixtureType.WRECKER_BODY) {
			wreckerIsA = false;
		} else {
			return;
		}

		long key = (((long) fDataA.getId()) << 32) + fDataB.getId();
		WorldManifold wm = contact.getWorldManifold();
		Vector2 box2dCollisionPoint = wm.getPoints()[0];

		WeaponWreckerHitEvent wwh = new WeaponWreckerHitEvent();
		wwh.wreckerFix = wreckerIsA ? contact.getFixtureA() : contact.getFixtureB();
		wwh.weaponFix = wreckerIsA ? contact.getFixtureB() : contact.getFixtureA();
		wwh.targetWrecker = ((Entity) wwh.wreckerFix.getBody().getUserData());
		wwh.weapon = ((Entity) wwh.weaponFix.getBody().getUserData());
		wwh.point = new Vector2(box2dCollisionPoint).scl(Game.scale);
		wwh.normal = new Vector2(calculatePlayerNormal(wwh.wreckerFix, box2dCollisionPoint, wm.getNormal(), !wreckerIsA));
		wwh.collisionVelocity = new Vector2(new Vector2(wwh.weaponFix.getBody().getLinearVelocityFromWorldPoint(box2dCollisionPoint)).sub(wwh.wreckerFix.getBody().getLinearVelocityFromWorldPoint(box2dCollisionPoint)));

		float dullness = dullnessLogisticFunction(calculateDullness(wwh.collisionVelocity, wwh.normal));
		float sliceness = 1 - dullness;
		float sharpness = 0;
		FixtureData fData = ((FixtureData) wwh.weaponFix.getUserData());
		if (fData instanceof WeaponPiercingFD) {
			sharpness = sharpnessLogisticFunction(calculateSharpness(((WeaponPiercingFD) fData), wwh.weaponFix, wwh.collisionVelocity));
			float oldDullness = dullness;
			dullness = oldDullness * (1 - sharpness);
			sharpness = oldDullness * sharpness;
		}

		wwh.dullness = dullness;
		wwh.sliceness = sliceness;
		wwh.sharpness = sharpness;

		weaponWreckerFrameEventMap.put(key, wwh);
	}

	private void postSolve(Contact contact, ContactImpulse contactImpulse) {
		float impulse = contactImpulse.getNormalImpulses()[0];
		FixtureData fDataA = (FixtureData) contact.getFixtureA().getUserData();
		FixtureData fDataB = (FixtureData) contact.getFixtureB().getUserData();

		if (fDataA.getFixtureType() == FixtureType.WEAPON_DAMAGE && fDataB.getFixtureType() == FixtureType.WEAPON_DAMAGE) {
			handleWeaponToWeapon(((Entity) contact.getFixtureA().getBody().getUserData()),
					((Entity) contact.getFixtureB().getBody().getUserData()),
					new Vector2(contact.getWorldManifold().getPoints()[0]).scl(Game.scale),
					impulse);
			return;
		}

		if ((fDataA.getFixtureType() == FixtureType.WRECKER_BODY && fDataB.getFixtureType() == FixtureType.WEAPON_DAMAGE)
				|| (fDataA.getFixtureType() == FixtureType.WEAPON_DAMAGE && fDataB.getFixtureType() == FixtureType.WRECKER_BODY)) {

			long key = (((long) fDataA.getId()) << 32) + fDataB.getId();
			WeaponWreckerHitEvent wwh = weaponWreckerFrameEventMap.remove(key);
			if (wwh == null) {
				Log.error("PSD not found for PostSolve event");
				return;
			}
			wwh.impulse = impulse;
			engine.dispatchLater(wwh);
		}
	}

	protected void handleWeaponToWeapon(Entity a, Entity b, Vector2 point, float impulse) {
		determineDisarm(a, b, point, impulse, (weapon, owner) -> {
			getEngine().dispatchLater(new DetachRequest(DetachRequest.Type.TARGET_WEAPON, owner, weapon));
			if (owner != null) {
				StatusEffectComponent sec = owner.get(M.effect);
				if (sec != null) {
					getEngine().add(new EntityString("Disarm!", 1, point.x, point.y + 25, Color.ORANGE));
					sec.add(new DisarmStatusEffect(4));
				}
			}
		});
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

		final float minToDisarm = 30;
		boolean disarm1 = impulseForce * (wc2 == null ? 1 : wc2.disarmAbility) * leagueFormula(disarmResist1) > minToDisarm;
		boolean disarm2 = impulseForce * (wc1 == null ? 1 : wc1.disarmAbility) * leagueFormula(disarmResist2) > minToDisarm;

		if (impulseForce * leagueFormula(disarmResist2) > 1) { //Log disarm values
			float disarmAValue = impulseForce * leagueFormula(disarmResist1) * (wc2 == null ? 1 : wc2.disarmAbility);
			float disarmBValue = impulseForce * leagueFormula(disarmResist2) * (wc1 == null ? 1 : wc1.disarmAbility);
			Log.debug("Disarm stats: " + disarmAValue + " : " + disarmBValue + " -- " + StringUtils.ff(minToDisarm));
		}

		if (disarm1) {
			weaponOwnerConsumer.accept(weaponA, owner1);
		}
		if (disarm2) {
			weaponOwnerConsumer.accept(weaponB, owner2);
		}
	}

}
