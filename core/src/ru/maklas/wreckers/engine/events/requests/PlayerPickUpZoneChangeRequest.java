package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;

public class PlayerPickUpZoneChangeRequest {

    private boolean enable;
    private Entity entity;

    public PlayerPickUpZoneChangeRequest(boolean enable, Entity entity) {
        this.enable = enable;
        this.entity = entity;
    }

    public boolean state() {
        return enable;
    }

    public Entity getEntity() {
        return entity;
    }
}
