package ru.maklas.wreckers.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ru.maklas.wreckers.libs.Utils;

/**
 * Created by maklas on 17.06.2017.
 */

public class ShapeBuilder {

    float scale;
    private Vector2[] squareVerts;


    public ShapeBuilder(float scale) {
        this.scale = scale;
        squareVerts = new Vector2[4];

        for (int i = 0; i < 4; i++) {
            squareVerts[i] = new Vector2();
        }
    }


    public PolygonShape buildRectangle(float x, float y, float width, float height){
        x /= scale;
        y /= scale;
        width /= scale;
        height /= scale;

        squareVerts[0].set(x, y);
        squareVerts[1].set(x + width, y);
        squareVerts[2].set(x + width, y + height);
        squareVerts[3].set(x, y + height);

        PolygonShape poly = new PolygonShape();
        poly.set(squareVerts);

        return poly;
    }

    public CircleShape buildCircle(float x, float y, float radius){
        CircleShape circle = new CircleShape();
        circle.setRadius(radius/scale);
        circle.setPosition(Utils.vec1.set(x/scale, y/scale));
        return circle;
    }

    public PolygonShape buildPlaneShape(){

        squareVerts[0].set(-0.6f, 0);
        squareVerts[1].set(-0.3f, -0.2f);
        squareVerts[2].set(0.6f, 0);
        squareVerts[3].set(-0.3f, 0.25f);

        PolygonShape shape = new PolygonShape();
        shape.set(squareVerts);
        return shape;
    }
}
