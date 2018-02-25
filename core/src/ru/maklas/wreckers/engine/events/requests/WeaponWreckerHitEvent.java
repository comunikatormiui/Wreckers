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
    private final Vector2 collisionVelocity;
    private final Vector2 weaponStuckPoint;
    float impulse;
    float sliceness;
    float dullness;
    float sharpness;
    private final Body weaponBody;
    private final Body wreckerBody;
    private Vector2 piercingDirection;

    public WeaponWreckerHitEvent(Entity weapon, @Nullable Entity weaponOwner, Entity targetWrecker, Vector2 point, Vector2 normal, Vector2 collisionVelocity, Vector2 piercingDirection, Vector2 weaponStuckPoint, float impulse, float sliceness, float dullness, float sharpness, Body weaponBody, Body wreckerBody) {
        this.weapon = weapon;
        this.weaponOwner = weaponOwner;
        this.targetWrecker = targetWrecker;
        this.point = point;
        this.collisionVelocity = collisionVelocity;
        this.piercingDirection = piercingDirection;
        this.weaponStuckPoint = weaponStuckPoint;
        this.impulse = impulse;
        this.normal = normal;
        this.sliceness = sliceness;
        this.dullness = dullness;
        this.sharpness = sharpness;
        this.weaponBody = weaponBody;
        this.wreckerBody = wreckerBody;
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

    /**
     * Vector pointing from body to the weapon
     */
    public Vector2 getNormal() {
        return normal;
    }

    public Vector2 getCollisionVelocity() {
        return collisionVelocity;
    }

    public float getSliceness() {
        return sliceness;
    }

    public float getSharpness() {
        return sharpness;
    }

    public float getDullness() {
        return dullness;
    }

    public Body getWeaponBody() {
        return weaponBody;
    }

    public Body getWreckerBody() {
        return wreckerBody;
    }

    public Vector2 getPiercingDirection() {
        return piercingDirection;
    }

    public Vector2 getWeaponStuckPoint() {
        return weaponStuckPoint;
    }
}
