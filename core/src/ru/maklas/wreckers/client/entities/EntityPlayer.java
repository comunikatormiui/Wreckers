package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.*;

public class EntityPlayer extends GameEntity {

    private Body body;

    public EntityPlayer(int id, float x, float y, float health, ClientGameModel model, EntityType eType) {
        super(id, eType, x, y, GameAssets.playerZ);

        float bodyRadius = 25;
        float pickUpRadius = 45;


        FixtureDef bodyF = model.getFixturer()
                .newFixture()
                .shape(model.getShaper()
                        .buildCircle(0, 0, bodyRadius))
                .friction(0.4f)
                .mask(eType)
                .bounciness(0.2f)
                .build();

        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(1f)
                .addFixture(bodyF)
                .build();

        add(new PhysicsComponent(body));
        add(new HealthComponent(health));
        add(new VelocityComponent(50));
        add(new AntiGravComponent(body.getMassData().mass,7, 10));
        add(new SocketComponent(1, EntityType.weaponTypeFor(eType)));
        add(new WielderPickUpZoneComponent(model.getShaper().buildCircle(0, 0, pickUpRadius)));
    }
}
