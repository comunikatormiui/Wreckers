package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import ru.maklas.mengine.Entity;

public interface AttachAction {

    JointDef attach(Entity owner, WSocket socket, Body ownerBody);

}
