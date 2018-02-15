package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.events.CollisionEvent;

public class CollisionComponent implements Component{

    public final Body body;
    public final Signal<CollisionEvent> signal = new Signal<CollisionEvent>();

    public CollisionComponent(Body body) {
        this.body = body;
    }


}
