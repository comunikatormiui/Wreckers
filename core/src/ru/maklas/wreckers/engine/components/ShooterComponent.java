package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Component;

public class ShooterComponent implements Component{

    public final float relativeX;
    public final float relativeY;
    public final Vector2 shootingPoint;
    public final Vector2 shootingDirection;

    public ShooterComponent(float relativeX, float relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        shootingPoint = new Vector2();
        shootingDirection = new Vector2(1, 0);
    }

}
