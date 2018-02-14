package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Component;

public class CollisionComponent implements Component{

    public Body body;

    public CollisionComponent(Body body) {
        this.body = body;
    }


    public CollisionComponent() {
    }
}
