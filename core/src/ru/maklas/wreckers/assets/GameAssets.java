package ru.maklas.wreckers.assets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.jetbrains.annotations.NotNull;

public class GameAssets {

    public static final float box2dScale = 50;
    private static final Vector2 vec = new Vector2();
    public static void rotateBody(@NotNull Body body, float targetX, float targetY){
        float angle = vec.set(body.getPosition()).scl(box2dScale).sub(targetX, targetY).angle();
        body.setTransform(body.getPosition(), (angle + 180) * MathUtils.degreesToRadians);
    }



    //************//

    //************//
    public static final int playerZ = 10;
    //* Z-Orders *//
    public static int bulletZ = 8;

    //*********//
    //* TYPES *//
    //*********//




}
