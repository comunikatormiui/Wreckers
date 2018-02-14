package ru.maklas.wreckers.engine.components;


import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.Component;

/**
 * Created by Danil on 15.08.2017.
 */

public class AccelerationComponent implements Component, Pool.Poolable {

    public float x, y;

    public AccelerationComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public AccelerationComponent() {

    }

    @Override
    public String toString() {
        return "AccC{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public AccelerationComponent setUp(float ax, float ay) {
        this.x = ax;
        this.y = ay;
        return this;
    }

    @Override
    public void reset() {
        x = y = 0;
    }

    public void set(float ax, float ay) {
        this.x = ax;
        this.y = ay;
    }
}
