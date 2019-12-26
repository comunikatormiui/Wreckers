package ru.maklas.wreckers.engine.weapon;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.wrecker.WSocket;

public interface AttachAction {

	JointDef attach(Entity owner, WSocket socket, Body ownerBody);

}
