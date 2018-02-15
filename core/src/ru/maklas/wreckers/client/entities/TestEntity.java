package ru.maklas.wreckers.client.entities;

import ru.maklas.mengine.UpdatableEntity;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.libs.Timer;

public class TestEntity extends UpdatableEntity {

    Timer timer = new Timer();

    public TestEntity(float x, float y, float ttl) {
        super(x, y, 0);

        RenderUnit unit = new RenderUnit(Images.point);
        unit.pivotX = 0.5f;
        unit.pivotY = 0.5f;
        add(new RenderComponent(unit));

        timer.setTime(ttl);
        timer.setAction(new Timer.Action() {
            @Override
            public boolean execute() {
                getEngine().remove(TestEntity.this);
                return false;
            }
        });
    }

    @Override
    public void update(float v) {
        timer.update(v);
    }
}
