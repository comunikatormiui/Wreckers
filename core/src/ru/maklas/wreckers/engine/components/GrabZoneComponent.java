package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import ru.maklas.mengine.Component;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;

public class GrabZoneComponent implements Component{

    /**
     * Definition of the fixture that allows grab
     */
    public final FixtureDef def;
    /**
     * Current fixture that allows grabbing
     */
    public Fixture fixture;

    public GrabZoneComponent(FixtureDef def) {
        this.def = def;
    }

    public GrabZoneComponent(Shape shape) {
        def = new FixtureDef();
        def.isSensor = true;
        def.shape = shape;
        def.filter.categoryBits = EntityType.GRABBER.category;
        def.filter.maskBits = EntityType.GRABBER.mask;
    }
    public GrabZoneComponent(float circleRadius, float localX, float localY){
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circleRadius / GameAssets.box2dScale);
        circleShape.setPosition(new Vector2(localX, localY).scl(1/GameAssets.box2dScale));

        def = new FixtureDef();
        def.isSensor = true;
        def.shape = circleShape;
        def.filter.categoryBits = EntityType.GRABBER.category;
        def.filter.maskBits = EntityType.GRABBER.mask;
    }

    /**
     * Whether or not current Entity has Grab zone enabled
     * @return
     */
    public boolean enabled(){
        return fixture != null;
    }
}
