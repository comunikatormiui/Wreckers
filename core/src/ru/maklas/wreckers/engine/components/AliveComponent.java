package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

/**
 * Created by Danil on 17.08.2017.
 */

public class AliveComponent implements Component {

    public boolean isAlive;

    public AliveComponent(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public AliveComponent setUp(boolean alive){
        this.isAlive = alive;
        return this;
    }

    public AliveComponent() {

    }

    @Override
    public String toString() {
        return "AliveC{" +
                isAlive +
                '}';
    }
}
