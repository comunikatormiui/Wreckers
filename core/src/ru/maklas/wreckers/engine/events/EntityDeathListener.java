package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public abstract class EntityDeathListener implements Listener<DeathEvent>{

    private final Entity entity;

    public EntityDeathListener(Entity entity) {
        this.entity = entity;
    }

    @Override
    public final void receive(Signal<DeathEvent> signal, DeathEvent deathEvent) {
        if (!entity.isInEngine()){
            signal.remove(this);
        }
        if (entity == deathEvent.getTarget()){
            process(signal, deathEvent, entity);
        }
    }


    public abstract void process(Signal<DeathEvent> signal, DeathEvent deathEvent, Entity entity);



}
