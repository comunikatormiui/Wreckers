package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;

public class EntityScythe extends WeaponEntity implements AttachAction {


    Body body;
    World world;
    PickUpComponent pickUpC;
    final float scale = 0.65f;

    public EntityScythe(int id, float x, float y, int zOrder, ClientGameModel model) {
        super(id, x, y, zOrder, model);
        this.world = model.getWorld();
        final EntityType eType = EntityType.NEUTRAL_WEAPON;

        PolygonShape handle = new PolygonShape();
        PolygonShape sharp = new PolygonShape();
        PolygonShape sharp2 = new PolygonShape();
        PolygonShape sharp3 = new PolygonShape();

        Vector2[] pointsHandle = new Vector2[]{
                new Vector2(34, 226),
                new Vector2(108, 74),
                new Vector2(124, 84),
                new Vector2(95, 159),
                new Vector2(49, 235),
                new Vector2(34, 226)
        };

        Vector2[] pointsSharp1 = new Vector2[]{
                new Vector2(108, 74),
                new Vector2(101, 11),
                new Vector2(152, 23),
                new Vector2(221, 64),
                new Vector2(194, 106)
        };

        Vector2[] pointsSharp2 = new Vector2[]{
                new Vector2(194, 106),
                new Vector2(240, 108),
                new Vector2(243, 166),
                new Vector2(217, 198)
        };

        Vector2[] pointsSharp3 = new Vector2[]{
                new Vector2(220, 146),
                new Vector2(194, 106),
                new Vector2(221, 64)
        };


        Vector2 v = new Vector2(1, -1);

        for (Vector2 point : pointsHandle) {
            point
                    .scl(v)
                    .add(0, 256)
                    .scl(scale / (GameAssets.box2dScale));
        }

        for (Vector2 point : pointsSharp1) {
            point.scl(v).add(0, 256).scl(scale / (GameAssets.box2dScale));
        }

        for (Vector2 point : pointsSharp2) {
            point.scl(v).add(0, 256).scl(scale / (GameAssets.box2dScale));
        }

        for (Vector2 point : pointsSharp3) {
            point.scl(v).add(0, 256).scl(scale / (GameAssets.box2dScale));
        }

        handle.set(pointsHandle);
        sharp.set(pointsSharp1);
        sharp2.set(pointsSharp2);
        sharp3.set(pointsSharp3);

        FixtureDef fix = model.getFixturer().newFixture()
                .bounciness(0.1f)
                .mask(eType)
                .friction(1f)
                .shape(handle)
                .density(1)
                .build();

        FixtureDef fix2 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(sharp)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(8)
                .build();

        FixtureDef fix3 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(sharp2)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        FixtureDef fix4 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(sharp3)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(fix, new FixtureData(FixtureType.WEAPON_NO_DAMAGE))
                .addFixture(fix2, new FixtureData(FixtureType.WEAPON_DAMAGE))
                .addFixture(fix3, new WeaponPiercingFD(0.5f, -2f, 217 * scale / GameAssets.box2dScale, 60 * scale / GameAssets.box2dScale))
                .addFixture(fix4, new FixtureData(FixtureType.WEAPON_DAMAGE))
                .angularDamp(0.1f)
                .build();

        System.out.println(id + ": Scythe mass " + body.getMass());

        RenderUnit unit = new RenderUnit(Images.scythe);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = unit.pivotY = 0;


        add(new PhysicsComponent(body));
        add(new RenderComponent(unit));
        pickUpC = new PickUpComponent(model.getShaper().buildCircle(38 * scale, 20 * scale, 35), this);
        add(pickUpC);
        add(new WeaponComponent(
                10,
                50,
                50,
                1,
                1,
                1,
                12,
                55,
                25));
    }

    @Override
    public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
        RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = ownerBody;
        rjd.bodyB = this.body;
        rjd.localAnchorA.set(socket.localX, socket.localY);
        rjd.localAnchorB.set(38, 20).scl(scale / GameAssets.box2dScale);
        return rjd;
    }
}
