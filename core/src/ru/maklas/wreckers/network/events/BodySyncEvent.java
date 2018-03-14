package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class BodySyncEvent implements Copyable {
    
    int entityId;
    float x;
    float y;
    float velX;
    float velY;
    
    public BodySyncEvent (int entityId, float x, float y, float velX, float velY) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
    }
    
    public BodySyncEvent () {
        
    }
    
    public BodySyncEvent setAndRet(int entityId, float x, float y, float velX, float velY) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        return this;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getVelX() {
        return this.velX;
    }
    
    public float getVelY() {
        return this.velY;
    }
    
    
    
    @Override
    public String toString() {
        return "BodySyncEvent{" +
        "entityId=" + entityId +
        ", x=" + x +
        ", y=" + y +
        ", velX=" + velX +
        ", velY=" + velY +
        '}';
    }
    
    @Override
    public Object copy() {
        return new BodySyncEvent(entityId, x, y, velX, velY);
    }
}
