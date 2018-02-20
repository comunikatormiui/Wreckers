package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;

public class GrabZoneChangeRequest {

    private boolean enable;
    private Entity entity;

    public GrabZoneChangeRequest(boolean enable, Entity entity) {
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
