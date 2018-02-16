package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class HealthComponent implements Component{

    public float health;
    public boolean dead = false;

    public HealthComponent(float health) {
        this.health = health;
    }

}
