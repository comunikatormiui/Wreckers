package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class AntiGravComponent implements Component{


    public float maxX;
    public float maxY;
    public float dX;
    public float dY;
    public float changeSpeed;
    public boolean directionUp = true;
    public boolean directionRight = true;
    public boolean enabled = true;
    public float mass;


    public AntiGravComponent(float mass, float maxX, float maxY, float changeSpeed) {
        this.mass = mass;
        this.maxX = maxX;
        this.maxY = maxY;
        this.changeSpeed = changeSpeed;
    }



}
