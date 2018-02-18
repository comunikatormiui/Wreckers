package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class PlayerComponent implements Component{

    public float deltaY;
    public float maxY;
    public float speed;
    public boolean directionUp = true;


    public PlayerComponent(float maxY, float speed) {
        this.maxY = maxY;
        this.speed = speed;
    }
}
