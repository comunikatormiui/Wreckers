package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Entity;

/**
 * Сокет с оружием
 */
public class WSocket {

    public Entity attachedEntity;
    public float localX;
    public float localY;

    public boolean isEmpty(){
        return attachedEntity == null;
    }

}
