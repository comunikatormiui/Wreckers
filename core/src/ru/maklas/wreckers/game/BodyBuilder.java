package ru.maklas.wreckers.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import ru.maklas.wreckers.game.fixtures.FixtureData;

public class BodyBuilder {

    private World world;
    private BodyDef bDef = new BodyDef();
    private Array<FixtureDef> fixDefs = new Array<FixtureDef>();
    private Array<ru.maklas.wreckers.game.fixtures.FixtureData> datas = new Array<ru.maklas.wreckers.game.fixtures.FixtureData>();
    private boolean building = false;
    private float scale;

    public BodyBuilder(World world, float box2dScale) {
        this.world = world;
        this.scale = box2dScale;
    }





    public BodyBuilder newBody(){
        if (building){
            throw new RuntimeException("newBody(); before build();");
        }

        building = true;
        restoreDefs();
        return this;
    }

    public BodyBuilder addFixture(FixtureDef fDef){
        return addFixture(fDef, null);
    }

    public BodyBuilder addFixture(FixtureDef fDef, FixtureData data){
        fixDefs.add(fDef);
        datas.add(data);
        return this;
    }

    public Body build(){
        if (!building){
            throw new RuntimeException("build(); before newBody();");
        }
        building = false;

        Body body = world.createBody(bDef);
        int size = fixDefs.size;
        for (int i = 0; i < size; i++) {
            Fixture fixture = body.createFixture(fixDefs.get(i));
            fixture.setUserData(datas.get(i));
        }

        return body;
    }




    // PARAMETERS


    public BodyBuilder pos(float x, float y){
        bDef.position.set(x/scale, y/scale);
        return this;
    }

    public BodyBuilder vel(float x, float y){
        bDef.linearVelocity.set(x/scale, y/scale);
        return this;
    }

    public BodyBuilder vel(Vector2 v){
        bDef.linearVelocity.set(v.x/scale, v.y/scale);
        return this;
    }

    public BodyBuilder angVel(float toTheLeft){
        bDef.angularVelocity = toTheLeft;
        return this;
    }

    public BodyBuilder angle(float angle){
        bDef.angle = angle;
        return this;
    }

    public BodyBuilder type(BodyDef.BodyType type){
        bDef.type = type;
        return this;
    }

    public BodyBuilder fixRotation(){
        bDef.fixedRotation = true;
        return this;
    }

    public BodyBuilder setBullet(){
        bDef.bullet = true;
        return this;
    }

    public BodyBuilder linearDamp(float damping){
        bDef.linearDamping = damping;
        return this;
    }

    public BodyBuilder gravityScale(float scale){
        bDef.gravityScale = scale;
        return this;
    }

    public BodyBuilder angularDamp(float damping){
        bDef.angularDamping = damping;
        return this;
    }

    private void restoreDefs(){
        bDef.linearVelocity.set(0, 0);
        bDef.fixedRotation = false;
        bDef.angularVelocity = 0.0001f;
        bDef.angularDamping = 0;
        bDef.angle = 0;
        bDef.bullet = false;
        bDef.gravityScale = 1;
        bDef.awake = true;
        bDef.fixedRotation = false;
        bDef.active = true;
        bDef.position.set(0, 0);
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.linearDamping = 0;

        fixDefs.clear();
        datas.clear();
    }

}
