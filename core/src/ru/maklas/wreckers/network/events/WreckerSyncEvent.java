package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class WreckerSyncEvent implements Copyable {
    
    BodySyncEvent pos;
    float motorX;
    float motorY;
    
    public WreckerSyncEvent (BodySyncEvent pos, float motorX, float motorY) {
        this.pos = pos;
        this.motorX = motorX;
        this.motorY = motorY;
    }
    
    public WreckerSyncEvent () {
        
    }
    
    public WreckerSyncEvent setAndRet(BodySyncEvent pos, float motorX, float motorY) {
        this.pos = pos;
        this.motorX = motorX;
        this.motorY = motorY;
        return this;
    }
    
    public BodySyncEvent getPos() {
        return this.pos;
    }
    
    public float getMotorX() {
        return this.motorX;
    }
    
    public float getMotorY() {
        return this.motorY;
    }

    public int getId(){
        return pos.getId();
    }
    
    
    
    @Override
    public String toString() {
        return "WreckerSyncEvent{" +
        "pos=" + pos +
        ", motorX=" + motorX +
        ", motorY=" + motorY +
        '}';
    }
    
    @Override
    public Object copy() {
        return new WreckerSyncEvent((BodySyncEvent) pos.copy(), motorX, motorY);
    }
}
