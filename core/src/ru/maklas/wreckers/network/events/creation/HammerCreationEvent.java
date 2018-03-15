package ru.maklas.wreckers.network.events.creation;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class HammerCreationEvent extends WeaponCreationEvent implements Copyable {


    public HammerCreationEvent(int id, float x, float y, float angle) {
        super(id, x, y, angle);
    }

    public HammerCreationEvent () {

    }

    public HammerCreationEvent setAndRet() {
        return this;
    }


    @Override
    public String toString() {
        return "HammerCreationEvent{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                '}';
    }

    @Override
    public Object copy() {
        return new HammerCreationEvent(id, x, y, angle);
    }
}
