package ru.maklas.wreckers.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.wreckers.libs.Utils;

public class BodyBuilder {

    private World world;
    private ShapeBuilder builder;
    private final FixtureDef fDef;
    private final BodyDef bDef;
    private float scale;
    private static float FRICTION = 1;
    private static float LINEAR_DUMPING = 1;
    private static float ANGULAR_DAMPING = 0.1f;
    private static float DENSITY = 1;





    public BodyBuilder(World world, ShapeBuilder shapeBuilder) {
        fDef = new FixtureDef();
        bDef = new BodyDef();
        this.world = world;
        this.builder = shapeBuilder;
        this.scale = shapeBuilder.scale;
    }


    /**
     * builds rectangle with bot-left align
     */
    public Body buildRectangle(float x, float y, float localX, float localY, float width, float height, float speedX, float speedY, boolean dynamicBody, boolean fixedRotation, float linearDumping, short category, short collisionMask, float mass){

        bDef.position.set(x/scale, y/scale);
        bDef.linearDamping = linearDumping;
        bDef.type = dynamicBody? BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;
        bDef.fixedRotation = fixedRotation;
        bDef.linearVelocity.set(speedX/scale, speedY/scale);

        PolygonShape box = builder.buildRectangle(localX, localY, width, height);
        fDef.shape = box;
        fDef.density = DENSITY;
        fDef.filter.categoryBits = category;
        fDef.filter.maskBits = collisionMask;
        fDef.restitution = 0.2f;

        Body body = world.createBody(bDef);
        body.createFixture(fDef);

        if (mass > 0) {
            MassData m = body.getMassData();
            m.mass = mass;
            body.setMassData(m);
        }

        restoreDefs();
        return body;
    }

    public Body buildCircle(float x, float y, float localX, float localY, float radius, boolean dynamicBody, short category, short collisionMask, float mass){
        bDef.position.set(x/scale, y/scale);
        bDef.linearDamping = LINEAR_DUMPING;
        bDef.type = dynamicBody? BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;

        CircleShape shape = builder.buildCircle(localX, localY, radius);
        fDef.shape = shape;
        fDef.filter.categoryBits = category;
        fDef.filter.maskBits = collisionMask;

        Body body = world.createBody(bDef);
        body.createFixture(fDef);


        if (mass > 0) {
            MassData m = body.getMassData();
            m.mass = mass;
            body.setMassData(m);
        }

        restoreDefs();
        return body;
    }

    /**
     * bottom-center align
     */
    public Body buildPawn(float x, float y, float width, float height, boolean fixedRotation, short category, short collisionMask, float mass){

        float radius = (width * 0.8f)/2;

        CircleShape circleShape = builder.buildCircle(0, radius, radius);
        PolygonShape boxShape = builder.buildRectangle(-width/2, radius, width, height - radius);

        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(x/scale, y/scale);
        bDef.fixedRotation = fixedRotation;
        bDef.linearDamping = LINEAR_DUMPING;
        Body body = world.createBody(bDef);

        fDef.density = DENSITY;
        fDef.friction = FRICTION;
        fDef.filter.categoryBits = category;
        fDef.filter.maskBits = collisionMask;
        fDef.shape = boxShape;

        Fixture boxF = body.createFixture(fDef);
        fDef.shape = circleShape;
        Fixture wheel = body.createFixture(fDef);


        if (mass > 0) {
            MassData m = body.getMassData();
            m.mass = mass;
            body.setMassData(m);
        }

        restoreDefs();
        return body;
    }

    public Body buildPlaneBody(float x, float y, float speedX, float speedY, short category, short collisionMask, float mass){


        PolygonShape planeShape = builder.buildPlaneShape();
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(x/scale, y/scale);
        bDef.angle = Utils.vec1.set(speedX, speedY).angle() / MathUtils.radDeg;
        bDef.linearVelocity.set(speedX/scale, speedY/scale);
        bDef.angularDamping = 5f;

        Body body = world.createBody(bDef);


        fDef.filter.categoryBits = category;
        fDef.filter.maskBits = collisionMask;
        fDef.shape = planeShape;
        fDef.density = DENSITY;
        body.createFixture(fDef);


        if (mass > 0) {
            MassData m = body.getMassData();
            m.mass = mass;
            body.setMassData(m);
        }

        restoreDefs();

        return body;
    }

    private void restoreDefs(){
        fDef.isSensor = false;

        fDef.filter.categoryBits = 1;
        fDef.filter.maskBits = -1;
        fDef.shape = null;
        fDef.density = 0.0f;
        fDef.friction = 0.2f;
        fDef.restitution = 0.0f;

        bDef.linearVelocity.set(0, 0);
        bDef.fixedRotation = false;
        bDef.angularVelocity = 0.0f;
        bDef.angularDamping = 0;
        bDef.angle = 0;
        bDef.bullet = false;
        bDef.gravityScale = 1;
        bDef.position.set(0, 0);
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.linearDamping = 0;
    }

}
