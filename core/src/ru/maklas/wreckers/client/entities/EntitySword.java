package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;

public class EntitySword extends WeaponEntity {


    public EntitySword(int id, EntityType eType, float x, float y, int zOrder, ClientGameModel model) {
        super(id, eType, x, y, zOrder, model);

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


        Body build = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(fix)
                .addFixture(fix2)
                .angVel(1f)
                .angularDamp(0.1f)
                .build();

        MassData massData = build.getMassData();
        System.out.println(massData.I);
        build.setMassData(massData);

        RenderUnit unit = new RenderUnit(Images.sword);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = unit.pivotY = 0;


        add(new PhysicsComponent(build));
        add(new RenderComponent(unit));
    }
}
