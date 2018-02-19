package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Entity;

public interface AttachAction {

    boolean attach(Entity e, WSocket socket, Body body);
    boolean detach();

}
