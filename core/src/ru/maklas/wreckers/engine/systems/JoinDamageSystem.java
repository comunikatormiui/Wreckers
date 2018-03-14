package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.client.entities.EntityNumber;
import ru.maklas.wreckers.client.entities.EntityString;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.others.StunEffect;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.network.events.NetHitEvent;

import java.util.Random;

/**
 * Система принимает ивенты от сервера о нанесенном уроне.
 * Диспатчит внутри движка ивент о нанесенном уроне.
 */
public class JoinDamageSystem extends EntitySystem {

    public JoinDamageSystem() {

    }

    @Override
    public void onAddedToEngine(final Engine engine) {
       subscribe(new Subscription<NetHitEvent>(NetHitEvent.class) {
           @Override
           public void receive(Signal<NetHitEvent> signal, NetHitEvent hitEvent) {
               Entity player = engine.getById(hitEvent.getPlayerId());
               if (player == null){
                   return;
               }
               HealthComponent hc = player.get(Mappers.healthM);
               if (hc == null){
                   return;
               }

               hc.health = hitEvent.getNewHealth();
               hc.lastDamageDone = System.currentTimeMillis();
               getEngine().dispatch(new DamageEvent(player, hitEvent.getDamage(), null));
               if (hitEvent.died()){
                   hc.dead = true;
                   getEngine().dispatchLater(new DeathEvent(player, null));
               }

               test(hitEvent, player);
           }

       });
    }

    private Random rand = new Random();
    private void test(NetHitEvent e, Entity player){
        getEngine().add(new EntityNumber((int) e.getDamage(), 2, e.getX(), e.getY()));
        if (e.doStun()){
            getEngine().add(new EntityString("STUN! " + Utils.floatFormatted(e.getStunDuration()), 2, e.getX(), e.getY() + 50, Color.RED));

            player.get(Mappers.effectM).add(new StunEffect(e.getStunDuration()));
        }


        getEngine().add(new EntityString(
                (int)(e.getDullness() * 100) + " / " +
                        (int) (e.getSliceness() * 100) + " / " +
                        (int) (e.getSharpness() * 100), 2, e.getX() + (rand.nextFloat() * 50 - 25), e.getY() + 25, Color.PINK));
    }
}
