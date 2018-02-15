package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public abstract class EntityCollisionEvent implements Listener<CollisionEvent>{

    Entity entity;

    public EntityCollisionEvent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void receive(Signal<CollisionEvent> signal, CollisionEvent collisionEvent) {
        if (collisionEvent.a == entity){
            process(signal, collisionEvent);
        }
    }


    public abstract void process(Signal<CollisionEvent> signal, CollisionEvent collisionEvent);
}
