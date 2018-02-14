package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

/**
 * Created by Danil on 15.08.2017.
 */

public class VelocityComponent implements Component {

    public float x, y;

    public VelocityComponent() {

    }

    public VelocityComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "VelocityC{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void set(float vx, float vy) {
        this.x = vx;
        this.y = vy;
    }

    public VelocityComponent setUp(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
