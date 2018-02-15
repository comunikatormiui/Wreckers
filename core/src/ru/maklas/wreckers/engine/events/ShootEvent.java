package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

public class ShootEvent {

    Entity shooter;

    public ShootEvent(Entity shooter) {
        this.shooter = shooter;
    }

    public Entity getShooter() {
        return shooter;
    }
}
