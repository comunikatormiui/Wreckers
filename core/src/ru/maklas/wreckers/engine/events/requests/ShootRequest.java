package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;

public class ShootRequest {

    Entity shooter;

    public ShootRequest(Entity shooter) {
        this.shooter = shooter;
    }

    public Entity getShooter() {
        return shooter;
    }
}
