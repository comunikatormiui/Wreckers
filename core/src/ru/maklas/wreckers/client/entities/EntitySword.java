package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.AttachAction;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.engine.components.WeaponPickUpComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;

public class EntitySword extends WeaponEntity implements AttachAction {

    Body body;
    World world;

    public EntitySword(int id, EntityType eType, float x, float y, int zOrder, ClientGameModel model) {
        super(id, eType, x, y, zOrder, model);
        this.world = model.getWorld();
        final float scale = 0.15f;

        PolygonShape polygonShape = new PolygonShape();
        PolygonShape polygonShape1 = new PolygonShape();

        Vector2[] pointsHandle = new Vector2[]{
                new Vector2(400, 350),
                new Vector2(516, 235),
                new Vector2(516, 354 - 235),
                new Vector2(400, 354 - 350),
        };

        Vector2[] pointsSharp = new Vector2[]{
                new Vector2(516, 235),
                new Vector2(1377, 217),
                new Vector2(1500, 178),
                new Vector2(1377, 354 - 217),
                new Vector2(516, 354 - 235)
        };




        for (Vector2 point : pointsHandle) {
            point.scl(scale / (GameAssets.box2dScale));
        }

        for (Vector2 point : pointsSharp) {
            point.scl(scale / (GameAssets.box2dScale));
        }

        polygonShape.set(pointsHandle);
        polygonShape1.set(pointsSharp);

        FixtureDef fix = model.getFixturer().newFixture()
                .bounciness(0.1f)
                .mask(eType)
                .friction(0.2f)
                .shape(polygonShape)
                .density(0.1f)
                .build();

        FixtureDef fix2 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(polygonShape1)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(0.1f)
                .build();


        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(fix)
                .addFixture(fix2)
                .angVel(1f)
                .angularDamp(0.1f)
                .build();

        MassData massData = body.getMassData();
        System.out.println(massData.I);
        body.setMassData(massData);

        RenderUnit unit = new RenderUnit(Images.sword);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = unit.pivotY = 0;


        add(new PhysicsComponent(body));
        add(new RenderComponent(unit));
        WeaponPickUpComponent wpu = new WeaponPickUpComponent(model.getShaper().buildCircle(0, 0, 35), this);
        add(wpu);
        Fixture fixture = body.createFixture(wpu.def);
        fixture.setUserData(wpu);
    }

    @Override
    public void attach(Entity e, WSocket socket, Body body) {
        RopeJointDef rjd = new RopeJointDef();
        rjd.bodyA = body;
        rjd.bodyB = this.body;
        rjd.localAnchorA.set(socket.localX, socket.localY);
        rjd.localAnchorB.set(300, 178).scl(0.15f / GameAssets.box2dScale);
        world.createJoint(rjd);
    }

    @Override
    public void detach() {
        
    }
}
