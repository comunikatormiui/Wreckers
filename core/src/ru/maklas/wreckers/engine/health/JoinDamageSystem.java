package ru.maklas.wreckers.engine.health;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.SubscriptionSystem;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.status_effects.StunEffect;
import ru.maklas.wreckers.game.entities.EntityNumber;
import ru.maklas.wreckers.game.entities.EntityString;
import ru.maklas.wreckers.net_events.NetHitEvent;
import ru.maklas.wreckers.utils.StringUtils;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;
import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

import java.util.Random;

/**
 * Система принимает ивенты от сервера о нанесенном уроне.
 * Диспатчит внутри движка ивент о нанесенном уроне.
 */
public class JoinDamageSystem extends SubscriptionSystem {

	private Random rand = new Random();

	@Override
	public void onAddedToEngine(Engine engine) {
		NetDispatcher netD = engine.getBundler().get(B.netD);
		netD.subscribe(NetHitEvent.class, this::onNetHit);
	}

	private void onNetHit(Socket socket, NetHitEvent e) {
		Entity player = engine.findById(e.getPlayerId());
		if (player == null){
			return;
		}
		HealthComponent hc = player.get(M.health);
		if (hc == null){
			return;
		}

		hc.health = e.getNewHealth();
		hc.lastDamageDone = System.currentTimeMillis();
		getEngine().dispatch(new DamageEvent(player, e.getDamage(), null));
		if (e.died()){
			hc.alive = false;
			getEngine().dispatchLater(new DeathEvent(player, null));
		}

		test(e, player);
	}

	private void test(NetHitEvent e, Entity player){
		getEngine().add(new EntityNumber((int) e.getDamage(), 2, e.getX(), e.getY()));
		if (e.doStun()){
			getEngine().add(new EntityString("STUN! " + StringUtils.ff(e.getStunDuration()), 2, e.getX(), e.getY() + 50, Color.RED));

			player.get(M.effect).add(new StunEffect(e.getStunDuration()));
		}

		getEngine().add(new EntityString(
				(int)(e.getDullness() * 100) + " / " +
						(int) (e.getSliceness() * 100) + " / " +
						(int) (e.getSharpness() * 100), 2, e.getX() + (rand.nextFloat() * 50 - 25), e.getY() + 25, Color.PINK));
	}
}
