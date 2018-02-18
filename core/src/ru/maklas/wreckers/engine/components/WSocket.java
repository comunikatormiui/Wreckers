package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Body;

public class WSocket {

    public Body attachedBody;
    public float localX;
    public float localY;

    public boolean isEmpty(){
        return attachedBody == null;
    }

}
