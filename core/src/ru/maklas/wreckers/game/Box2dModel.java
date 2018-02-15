package ru.maklas.wreckers.game;

import com.badlogic.gdx.physics.box2d.World;

public class Box2dModel {


    private final World world;
    private final ShapeBuilder shapeBuilder;
    private final FDefBuilder fDefBuilder;
    private final BodyBuilder bodyBuilder;

    public Box2dModel(World world, ShapeBuilder shapeBuilder, FDefBuilder fDefBuilder, BodyBuilder bodyBuilder) {

        this.world = world;
        this.shapeBuilder = shapeBuilder;
        this.fDefBuilder = fDefBuilder;
        this.bodyBuilder = bodyBuilder;
    }


    public World getWorld() {
        return world;
    }

    public ShapeBuilder getShapeBuilder() {
        return shapeBuilder;
    }

    public FDefBuilder getfDefBuilder() {
        return fDefBuilder;
    }

    public BodyBuilder getBodyBuilder() {
        return bodyBuilder;
    }
}
