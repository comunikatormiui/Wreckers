package ru.maklas.wreckers.game;

import com.badlogic.gdx.physics.box2d.*;

public class FixtureBuilder {

    private final FixtureDef fDef = new FixtureDef();
    private final World world;
    private final ShapeBuilder builder;

    public FixtureBuilder(World world, ShapeBuilder builder) {
        this.world = world;
        this.builder = builder;
    }

    public FixtureDef buildRectangle(float localX, float localY,
                               float width, float height,
                               float density, int category, int collisionMask){

        FixtureDef fDef = new FixtureDef();

        fDef.shape = builder.buildRectangle(localX, localY, width, height);
        fDef.density = density;
        fDef.filter.categoryBits = (short) category;
        fDef.filter.maskBits = (short) collisionMask;
        fDef.restitution = 1f;

        return fDef;
    }

    public FixtureDef buildCircle(float localX, float localY,
                                  float radius,
                                  int category, int collisionMask){

        FixtureDef fDef = new FixtureDef();

        fDef.shape = builder.buildCircle(localX, localY, radius);
        fDef.filter.categoryBits = (short) category;
        fDef.filter.maskBits = (short) collisionMask;
        fDef.friction = 0;
        fDef.density = 0;
        fDef.restitution = 1f;

        return fDef;
    }

    private void restoreDefs() {
        FixtureDef fDef = this.fDef;

        fDef.isSensor = false;
        fDef.filter.categoryBits = 1;
        fDef.filter.maskBits = -1;
        fDef.shape = null;
        fDef.density = 0.0f;
        fDef.friction = 0.2f;
        fDef.restitution = 0.0f;
    }
}
