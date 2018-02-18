package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class AntiGravComponent implements Component{

    public float deltaY;
    public float maxY;
    public float speed;
    public boolean directionUp = true;
    public float mass;
    public boolean enabled = true;


    public AntiGravComponent(float mass, float maxY, float speed) {
        this.mass = mass;
        this.maxY = maxY;
        this.speed = speed;
    }

}
