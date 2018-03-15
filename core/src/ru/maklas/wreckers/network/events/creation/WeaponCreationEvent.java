package ru.maklas.wreckers.network.events.creation;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public abstract class WeaponCreationEvent implements Copyable {
    
    protected int id;
    protected float x;
    protected float y;
    protected float angle;
    
    public WeaponCreationEvent (int id, float x, float y, float angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
    
    public WeaponCreationEvent () {
        
    }
    
    public WeaponCreationEvent setAndRet(int id, float x, float y, float angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
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
    
    public float getAngle() {
        return this.angle;
    }
    
    
    
    @Override
    public String toString() {
        return "WeaponCreationEvent{" +
        "id=" + id +
        ", x=" + x +
        ", y=" + y +
        ", angle=" + angle +
        '}';
    }
    
    @Override
    public Object copy() {
        throw new RuntimeException();
    }
}
