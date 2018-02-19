package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.AttachAction;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.engine.components.WeaponPickUpComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;

public class EntityHammer extends WeaponEntity implements AttachAction {

    Body body;
    World world;
    WeaponPickUpComponent pickUpC;
    Joint lastJoint;

    public EntityHammer(int id, float x, float y, int zOrder, ClientGameModel model) {
        super(id, x, y, zOrder, model);
        this.world = model.getWorld();
        final float scale = 0.13f;
        final EntityType eType = EntityType.NEUTRAL_WEAPON;

        PolygonShape polygonShape = new PolygonShape();
        PolygonShape polygonShape1 = new PolygonShape();

        Vector2[] pointsHandle = new Vector2[]{
                new Vector2(8, 245),
                new Vector2(767, 254),
                new Vector2(767, 575 - 254),
                new Vector2(8, 575 - 245)
        };

        Vector2[] pointsBody = new Vector2[]{
                new Vector2(762, 8),
                new Vector2(1045, 8),
                new Vector2(1045, 575 - 8),
                new Vector2(762, 575 - 8)
        };




        for (Vector2 point : pointsHandle) {
            point.scl(scale / (GameAssets.box2dScale));
        }

        for (Vector2 point : pointsBody) {
            point.scl(scale / (GameAssets.box2dScale));
        }

        polygonShape.set(pointsHandle);
        polygonShape1.set(pointsBody);

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
                .density(0.01f)
                .build();


        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(fix)
                .addFixture(fix2)
                .angularDamp(0.1f)
                .build();

        MassData massData = body.getMassData();
        body.setMassData(massData);

        RenderUnit unit = new RenderUnit(Images.hammer);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = unit.pivotY = 0;


        add(new PhysicsComponent(body));
        add(new RenderComponent(unit));
        pickUpC = new WeaponPickUpComponent(model.getShaper().buildCircle(8 * scale, 287 * scale, 35), this);
        add(pickUpC);
    }

    @Override
    public boolean attach(Entity e, WSocket socket, Body body) {
        RopeJointDef rjd = new RopeJointDef();
        rjd.bodyA = body;
        rjd.bodyB = this.body;
        rjd.localAnchorA.set(socket.localX, socket.localY);
        rjd.localAnchorB.set(8, 287).scl(0.13f / GameAssets.box2dScale);
        lastJoint = world.createJoint(rjd);
        return true;
    }

    @Override
    public boolean detach() {
        if (lastJoint == null){
            return false;
        }
        world.destroyJoint(lastJoint);
        lastJoint = null;
        return true;
    }
}
