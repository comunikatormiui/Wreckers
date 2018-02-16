package ru.maklas.wreckers.server.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 16.02.2018
 */
public class PlayerPositionUpdate implements Copyable {
    
    int id;
    float x;
    float y;
    float xDir;
    float yDir;
    float xVel;
    float yVel;
    
    public PlayerPositionUpdate (int id, float x, float y, float xDir, float yDir, float xVel, float yVel) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xVel = xVel;
        this.yVel = yVel;
    }
    
    public PlayerPositionUpdate () {
        
    }
    
    public PlayerPositionUpdate setAndRet(int id, float x, float y, float xDir, float yDir, float xVel, float yVel) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xVel = xVel;
        this.yVel = yVel;
        return this;
    }
    
    public int getId() {
        return this.id;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getXDir() {
        return this.xDir;
    }
    
    public float getYDir() {
        return this.yDir;
    }
    
    public float getXVel() {
        return this.xVel;
    }
    
    public float getYVel() {
        return this.yVel;
    }
    
    
    
    @Override
    public String toString() {
        return "PlayerPositionUpdate{" +
        "id=" + id +
        ", x=" + x +
        ", y=" + y +
        ", xDir=" + xDir +
        ", yDir=" + yDir +
        ", xVel=" + xVel +
        ", yVel=" + yVel +
        '}';
    }
    
    @Override
    public Object copy() {
        return new PlayerPositionUpdate(id, x, y, xDir, yDir, xVel, yVel);
    }
}
