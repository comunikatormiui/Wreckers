package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;

public class HealthSystem extends EntitySystem{

    @Override
    public void onAddedToEngine(final Engine engine) {
        engine.subscribe(DamageEvent.class, new Listener<DamageEvent>(){
            @Override
            public void receive(Signal<DamageEvent> signal, DamageEvent damageEvent) {
                HealthComponent hc = damageEvent.getTarget().get(Mappers.healthM);
                if (hc != null && !hc.dead){
                    hc.health -= damageEvent.getDamage();
                    if (hc.health < 0){
                        hc.health = 0;
                        hc.dead = true;
                        engine.dispatch(new DeathEvent(damageEvent.getDamageDealer(), damageEvent.getTarget()));
                    }
                }
            }
        });
    }


}
