package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DamageSource;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.ShotEvent;

public class HealthSystem extends EntitySystem{

    @Override
    public void onAddedToEngine(final Engine engine) {

        Listener<ShotEvent> shotListener = new Listener<ShotEvent>() {
            @Override
            public void receive(Signal<ShotEvent> signal, ShotEvent shotEvent) {

                HealthComponent hc = shotEvent.getTarget().get(Mappers.healthM);
                if (hc != null && !hc.dead){
                    hc.health -= shotEvent.getDamage();

                    engine.dispatch(new DamageEvent(shotEvent.getInstigator(), DamageSource.BULLET_HIT, shotEvent.getTarget(), shotEvent.getDamage()));

                    if (hc.health < 0){
                        hc.health = 0;
                        hc.dead = true;
                        engine.dispatch(new DeathEvent(shotEvent.getInstigator(), DamageSource.BULLET_HIT, shotEvent.getTarget()));
                    }
                }
            }
        };

        engine.subscribe(ShotEvent.class, shotListener);
    }





}
