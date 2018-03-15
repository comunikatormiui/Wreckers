package ru.maklas.wreckers.network.events.creation;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class SwordCreationEvent extends WeaponCreationEvent implements Copyable {


    public SwordCreationEvent(int id, float x, float y, float angle) {
        super(id, x, y, angle);
    }

    public SwordCreationEvent () {
        
    }
    
    public SwordCreationEvent setAndRet() {
        return this;
    }


    @Override
    public String toString() {
        return "SwordCreationEvent{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                '}';
    }

    @Override
    public Object copy() {
        return new SwordCreationEvent(id, x, y, angle);
    }
}
