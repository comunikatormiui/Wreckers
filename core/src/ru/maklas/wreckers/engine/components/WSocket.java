package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Entity;

/**
 * ����� � �������
 */
public class WSocket {

    public Entity attachedEntity;
    public float localX;
    public float localY;

    public boolean isEmpty(){
        return attachedEntity == null;
    }

}
