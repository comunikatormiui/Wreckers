package ru.maklas.wreckers.engine.events.requests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.Event;

public class WeaponWreckerHitEvent implements Event {

    Entity weapon;
    @Nullable Entity weaponOwner;
    Entity targetWrecker;
    Vector2 point;
    Vector2 normal;
    float impulse;
    float sharpness;
    float dullness;
    private final Body weaponMass;
    private final Body wreckerMass;

    public WeaponWreckerHitEvent(Entity weapon, @Nullable Entity weaponOwner, Entity targetWrecker, Vector2 point, Vector2 normal, float impulse, float sharpness, float dullness, Body weaponMass, Body wreckerMass) {
        this.weapon = weapon;
        this.weaponOwner = weaponOwner;
        this.targetWrecker = targetWrecker;
        this.point = point;
        this.impulse = impulse;
        this.normal = normal;
        this.sharpness = sharpness;
        this.dullness = dullness;
        this.weaponMass = weaponMass;
        this.wreckerMass = wreckerMass;
    }

    public Entity getWeapon() {
        return weapon;
    }

    @Nullable
    public Entity getWeaponOwner() {
        return weaponOwner;
    }

    public Entity getTargetWrecker() {
        return targetWrecker;
    }

    public Vector2 getPoint() {
        return point;
    }

    public float getImpulse() {
        return impulse;
    }

    public Vector2 getNormal() {
        return normal;
    }

    public float getSharpness() {
        return sharpness;
    }

    public float getDullness() {
        return dullness;
    }

    public Body getWeaponBody() {
        return weaponMass;
    }

    public Body getWreckerBody() {
        return wreckerMass;
    }
}
