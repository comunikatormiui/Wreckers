package ru.maklas.wreckers.engine.health;

import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.other.Event;
import ru.maklas.wreckers.engine.physics.WeaponWreckerHitEvent;
import ru.maklas.wreckers.net_events.NetHitEvent;

/**
 * <p>
 *	 Система подписывается на HitEvent. Работает с HealthComponent. Обрабатывает момент удара,
 *	 рассчитывает урон защиту, принимает во внимание эффекты и генерирует DamageEvent, DeathEvent.
 * </p>
 * <p>
 *	 Вызывает эффекты возможные при ударе. Такие как стан, дополнительное отталкивание, застревание оружия в теле.
 * </p>
 */
public class HostDamageSystem extends DamageSystem {

	protected void applyDamageAndDispatch(Entity e, HealthComponent hc, float damage, Event hitEvent, float stunDuration){
		super.applyDamageAndDispatch(e, hc, damage, hitEvent, stunDuration);

		if (hitEvent instanceof WeaponWreckerHitEvent){
			WeaponWreckerHitEvent wwh = (WeaponWreckerHitEvent) hitEvent;
			Socket socket = engine.getBundler().get(B.socket);
			socket.send(new NetHitEvent(e.id, wwh.getWeapon().id, wwh.getPoint().x, wwh.getPoint().y, damage, hc.health, !hc.alive, wwh.getSliceness(), wwh.getDullness(), wwh.getSharpness(), stunDuration));
		}
	}

}
