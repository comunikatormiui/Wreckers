package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.*;

public class EntityPlayer extends GameEntity {

    private Body body;

    private static final int playerMass = 100;

    public EntityPlayer(int id, float x, float y, float health, ClientGameModel model, EntityType eType) {
        super(id, eType, x, y, GameAssets.playerZ);

        float bodyRadius = 40;
        float pickUpRadius = 75;
        float r = bodyRadius / GameAssets.box2dScale;
        float r2 = r * r;

        FixtureDef bodyF = model.getFixturer()
                .newFixture()
                .shape(model.getShaper()
                        .buildCircle(0, 0, bodyRadius))
                .friction(0.4f)
                .mask(eType)
                .bounciness(0.2f)
                .density(playerMass / (MathUtils.PI * r2))
                .build();

        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(1f)
                .addFixture(bodyF)
                .build();

        System.out.println(id + ": Player mass " + body.getMassData().mass);

        add(new PhysicsComponent(body));
        add(new HealthComponent(health));
        add(new MotorComponent(6000));
        add(new AntiGravComponent(body.getMassData().mass,5, 5, 1.2f));
        add(new SocketComponent(1, EntityType.weaponTypeFor(eType)));
        add(new GrabZoneComponent(model.getShaper().buildCircle(0, 0, pickUpRadius)));
    }
}
