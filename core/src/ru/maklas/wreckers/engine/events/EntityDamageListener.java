package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public abstract class EntityDamageListener implements Listener<DamageEvent>{

    private Entity entity;

    public EntityDamageListener(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void receive(Signal<DamageEvent> signal, DamageEvent damageEvent) {
        if (damageEvent.getTarget() == entity){
            process(signal, damageEvent);
        }
    }

    public abstract void process(Signal<DamageEvent> signal, DamageEvent damageEvent);

}
