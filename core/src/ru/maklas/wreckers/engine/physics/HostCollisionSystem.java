package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.status_effects.DisarmStatusEffect;
import ru.maklas.wreckers.engine.status_effects.StatusEffectComponent;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.game.entities.EntityString;

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
public class HostCollisionSystem extends CollisionSystem {

	protected void handleWeaponToWeapon(final Entity weaponA, Entity weaponB, Vector2 point, float impulse) {
		determineDisarm(weaponA, weaponB, point, impulse, (weapon, owner) -> {
			getEngine().dispatchLater(new DetachRequest(DetachRequest.Type.TARGET_WEAPON, null, weapon));
			if (owner != null) {
				StatusEffectComponent sec = owner.get(M.effect);
				if (sec != null) {
					getEngine().add(new EntityString("Disarm!", 1, point.x, point.y + 25, Color.ORANGE));
					sec.add(new DisarmStatusEffect(4));
				}
			}
		});
	}

	protected void handleWeaponToPlayer(PostCollisionEvent e, boolean weaponIsA) {
		doWeaponToPlayerHit(weaponIsA ? e : e.reverse(), engine);
	}
}
