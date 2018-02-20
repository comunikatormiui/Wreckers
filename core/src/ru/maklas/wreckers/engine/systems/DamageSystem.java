package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.requests.DamageRequest;

public class DamageSystem extends EntitySystem {

    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(new Subscription<DamageRequest>(DamageRequest.class) {
            @Override
            public void receive(Signal<DamageRequest> signal, DamageRequest damageRequest) {
                Entity target = damageRequest.getTarget();
                HealthComponent hc = target.get(Mappers.healthM);
                if (hc == null || hc.dead){
                    return;
                }

                hc.health -= damageRequest.getDamage();
                engine.dispatch(new DamageEvent(damageRequest.getType(), damageRequest.getDamage(), target, damageRequest.getDamageDealer(), damageRequest.getWeapon()));
                if (hc.health < 0){
                    hc.health = 0;
                    hc.dead = true;
                    engine.dispatch(new DeathEvent(target, damageRequest.getType(), damageRequest.getDamageDealer()));
                }

            }
        });
    }

}
