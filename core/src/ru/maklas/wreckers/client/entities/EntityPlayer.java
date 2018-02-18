package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.maklas.mengine.Engine;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PlayerComponent;
import ru.maklas.wreckers.engine.components.VelocityComponent;

public class EntityPlayer extends GameEntity {

    private Body body;
    private ClientGameModel model;
    private final EntityType eType;

    public EntityPlayer(int id, float x, float y, float health, ClientGameModel model, EntityType type) {
        super(id, type, x, y, GameAssets.playerZ);
        this.model = model;
        this.eType = type;

        float bodyRadius = 25;
        float pickUpRadius = 35;


        FixtureDef bodyF = model.getFixturer()
                .newFixture()
                .shape(model.getShaper()
                        .buildCircle(0, 0, bodyRadius))
                .friction(0.4f)
                .mask(eType)
                .bounciness(0.2f)
                .build();

        FixtureDef pickUp = model.getFixturer()
                .newFixture()
                .shape(model.getShaper()
                        .buildCircle(0, 0, pickUpRadius))
                .setSensor()
                .mask(eType)
                .build();

        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(1f)
                .addFixture(bodyF)
                .addFixture(pickUp)
                .build();


        add(new PhysicsComponent(body));
        add(new HealthComponent(health));
        add(new VelocityComponent(50));
        add(new PlayerComponent(7, 10));
    }

    @Override
    protected void addedToEngine(Engine engine) {

    }
}
