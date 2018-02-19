package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.components.TTLComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.libs.Utils;

public class EntityArrow extends Entity {


    private final Color color;
    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;
    private final float ttl;

    public EntityArrow(Color color, float x1, float y1, float x2, float y2, float ttl) {
        super();
        this.color = color;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.ttl = ttl;

        x = x1;
        y = y1;
    }

    public EntityArrow(float x1, float y1, float x2, float y2, float ttl) {
        this(Color.RED, x1, y1, x2, y2, ttl);
    }

    public EntityArrow(Vector2 start, Vector2 end, float ttl, Color color){
        this(color, start.x, start.y, end.x, end.y, ttl);
    }

    @Override
    protected void addedToEngine(Engine engine) {
        RenderUnit ru = new RenderUnit(Images.arrow);
        ru.width = Vector2.dst(x1, y1, x2, y2);
        ru.pivotX = 0;
        ru.pivotY = 0.5f;
        ru.angle = Utils.vec1.set(x2 - x1, y2 - y1).angle();
        RenderComponent rc = new RenderComponent(ru);
        rc.color = this.color;
        add(rc);
        add(new TTLComponent(ttl));
    }
}
