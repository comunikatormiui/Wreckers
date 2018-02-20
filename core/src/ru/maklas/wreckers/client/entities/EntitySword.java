package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.AttachAction;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;

public class EntitySword extends WeaponEntity implements AttachAction {

    Body body;
    World world;
    PickUpComponent pickUpC;

    public EntitySword(int id, float x, float y, int zOrder, ClientGameModel model) {
        super(id, x, y, zOrder, model);
        this.world = model.getWorld();
        final float scale = 0.15f;
        final EntityType eType = EntityType.NEUTRAL_WEAPON;

        PolygonShape handleShape = new PolygonShape();
        PolygonShape bladeShape = new PolygonShape();

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

        handleShape.set(pointsHandle);
        bladeShape.set(pointsSharp);

        FixtureDef handle = model.getFixturer().newFixture()
                .bounciness(0.3f)
                .mask(eType)
                .friction(0.2f)
                .shape(handleShape)
                .density(9.187957f) //0.3768188
                .build();

        FixtureDef blade = model.getFixturer().newFixture()
                .mask(eType)
                .shape(bladeShape)
                .friction(0.2f)
                .bounciness(0.3f)
                .density(9.187957f) //1.2557532
                .build();


        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(handle)
                .addFixture(blade)
                .angularDamp(0.1f)
                .build();

        System.out.println(id + ": Sword mass " + body.getMass());

        RenderUnit unit = new RenderUnit(Images.sword);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = unit.pivotY = 0;


        add(new PhysicsComponent(body));
        add(new RenderComponent(unit));
        pickUpC = new PickUpComponent(model.getShaper().buildCircle(400 * scale, 178 * scale, 35), this);
        add(pickUpC);
    }

    @Override
    public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
        RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = ownerBody;
        rjd.bodyB = this.body;
        rjd.localAnchorA.set(socket.localX, socket.localY);
        rjd.localAnchorB.set(300, 178).scl(0.15f / GameAssets.box2dScale);
        return rjd;
    }
}
