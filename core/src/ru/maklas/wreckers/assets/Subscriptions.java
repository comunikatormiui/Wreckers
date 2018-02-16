package ru.maklas.wreckers.assets;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.events.DeathEvent;

public class Subscriptions {

    public static Subscription<DeathEvent> removeOnDeathSubscription(final Entity entity){
        return new Subscription<DeathEvent>(DeathEvent.class) {
            @Override
            public void receive(Signal<DeathEvent> signal, DeathEvent deathEvent) {
                if (!entity.isInEngine()){
                    signal.remove(this);
                }
                if (entity == deathEvent.getTarget()){
                    entity.getEngine().remove(entity);
                }
            }
        };
    }


}
