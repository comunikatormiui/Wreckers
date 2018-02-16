package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

/**
 * Created by Danil on 15.08.2017.
 */

public class VelocityComponent implements Component {

    public float velocity;

    public VelocityComponent() {

    }

    public VelocityComponent(float velocity) {
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        return "VelocityC{" +
                "vel=" + velocity +
                '}';
    }

    public void set(float velocity) {
        this.velocity = velocity;
    }

    public VelocityComponent setUp(float velocity) {
        this.velocity = velocity;
        return this;
    }
}
