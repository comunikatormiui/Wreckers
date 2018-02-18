package ru.maklas.wreckers.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Pool;

public class ClosestRayCastResult implements RayCastCallback, Pool.Poolable {

    private float fraction = 1.1f;
    private Fixture fixture;
    private Vector2 point = new Vector2();
    private Vector2 normal = new Vector2();

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

        if (this.fraction > fraction){
            this.fraction = fraction;
            this.fixture = fixture;
            this.point.set(point);
            this.normal.set(normal);
        }

        return 1;
    }

    @Override
    public void reset(){
        this.fraction = 1.1f;
        this.fixture = null;
    }

    public boolean hit() {
        return fixture != null;
    }

    public float getFraction() {
        return fraction;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Vector2 getPoint() {
        return point;
    }

    public Vector2 getNormal() {
        return normal;
    }
}
