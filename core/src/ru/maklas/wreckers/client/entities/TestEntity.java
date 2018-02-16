package ru.maklas.wreckers.client.entities;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntity;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.components.TTLComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.libs.Timer;

public class TestEntity extends Entity {

    public TestEntity(float x, float y, float ttl) {
        this(x, y, 1000000, ttl);
    }

    public TestEntity(float x, float y, int zOrder, float ttl) {
        super(x, y, zOrder);
        add(new RenderComponent(Images.point));
        add(new TTLComponent(ttl));
    }
}
