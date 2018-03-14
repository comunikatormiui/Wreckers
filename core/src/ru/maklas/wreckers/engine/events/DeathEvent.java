package ru.maklas.wreckers.engine.events;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DeathEvent {

    Entity target;
    @Nullable private final Event lastHitEvent;

    public DeathEvent(Entity target, @Nullable Event lastHitEvent) {
        this.target = target;
        this.lastHitEvent = lastHitEvent;
    }

    public Entity getTarget() {
        return target;
    }

    @Nullable
    public Event getLastHitEvent() {
        return lastHitEvent;
    }
}
