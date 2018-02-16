package ru.maklas.wreckers.engine.events;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class ShotEvent {

    private final Entity instigator;
    private final Entity damageSource;
    private final Entity target;
    private float damage;
    private final Vector2 pos;
    private final float angle;


    public ShotEvent(@Nullable Entity instigator, Entity damageSource, Entity target, float damage, Vector2 pos, float angle) {
        this.instigator = instigator;
        this.damageSource = damageSource;
        this.target = target;
        this.damage = damage;
        this.pos = pos;
        this.angle = angle;
    }

    @Nullable
    public Entity getInstigator() {
        return instigator;
    }

    public float getDamage() {
        return damage;
    }

    public Entity getTarget() {
        return target;
    }

    public Entity getDamageSource() {
        return damageSource;
    }

    public Vector2 getPos() {
        return pos;
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return "ShotEvent{" +
                "instigator=" + instigator +
                ", damageSource=" + damageSource +
                ", target=" + target +
                ", damage=" + damage +
                ", pos=" + pos +
                ", angle=" + angle +
                '}';
    }
}
