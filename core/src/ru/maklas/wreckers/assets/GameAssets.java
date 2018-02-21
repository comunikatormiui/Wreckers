package ru.maklas.wreckers.assets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

public class GameAssets {

    public static final float box2dScale = 40;
    private static final Vector2 vec = new Vector2();
    public static void rotateBody(@NotNull Body body, float targetX, float targetY){
        float angle = vec.set(body.getPosition()).scl(box2dScale).sub(targetX, targetY).angle();
        body.setTransform(body.getPosition(), (angle + 180) * MathUtils.degreesToRadians);
    }



    public static final int playerZ = 10;
    public static final int zombieZ = 9;
    public static final int bulletZ = 8;

    public static void getEntityDirection(float angle, Vector2 direction) {
        direction.set(1, 0).setAngle(angle);
    }


    public static void setFilterData(@NotNull Body body, boolean includingSensors, EntityType type) {
        Array<Fixture> fixtureList = body.getFixtureList();
        for (Fixture fixture : fixtureList) {
            if (fixture.isSensor() && !includingSensors) {
                continue;
            }
            Filter filterData = fixture.getFilterData();
            filterData.categoryBits = type.category;
            filterData.maskBits     = type.mask;
            fixture.setFilterData(filterData);
        }
    }
}
