package ru.maklas.wreckers.network.events.creation;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class ScytheCreationEvent extends WeaponCreationEvent implements Copyable {

    public ScytheCreationEvent(int id, float x, float y, float angle) {
        super(id, x, y, angle);
    }
    
    public ScytheCreationEvent () {
        
    }
    
    public ScytheCreationEvent setAndRet() {
        return this;
    }



    @Override
    public String toString() {
        return "ScytheCreationEvent{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                '}';
    }
    
    @Override
    public Object copy() {
        return new ScytheCreationEvent(id, x, y, angle);
    }
}
