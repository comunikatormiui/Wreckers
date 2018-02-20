package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float impulseDamageMultiplier;
    public float additionalPush;

    public WeaponComponent(float impulseDamageMultiplier, float additionalPush) {
        this.impulseDamageMultiplier = impulseDamageMultiplier;
        this.additionalPush = additionalPush;
    }
}
