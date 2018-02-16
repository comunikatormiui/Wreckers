package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Subscriptions;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.VelocityComponent;
import ru.maklas.wreckers.engine.components.ZombieComponent;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.FDefBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;

public class ClientEntityZombie extends Entity {

    public ClientEntityZombie(int id, float x, float y, float health, ClientGameModel model) {
        super(id, x, y, GameAssets.zombieZ);
        EntityType eType = EntityType.ZOMBIE;
        this.type = eType.type;

        ShapeBuilder shaper = model.getShaper();
        BodyBuilder builder = model.getBuilder();
        FDefBuilder fixturer = model.getFixturer();

        FixtureDef bodyF = fixturer.newFixture()
                .shape(shaper.buildCircle(0, 0, 20))
                .friction(0.4f)
                .mask(eType)
                .build();

        FixtureDef leftArmF = fixturer.newFixture()
                .shape(shaper.buildCircle(0, 22, 10))
                .friction(0.4f)
                .mask(eType)
                .build();

        FixtureDef rightArmF = fixturer.newFixture()
                .shape(shaper.buildCircle( 0, -22, 10))
                .friction(0.4f)
                .mask(eType)
                .build();

        Body body = builder.newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(5)
                .addFixture(bodyF)
                .addFixture(leftArmF)
                .addFixture(rightArmF)
                .build();

        add(new PhysicsComponent(body));
        add(new HealthComponent(health));
        add(new ZombieComponent(1.5f));
        add(new VelocityComponent(15));
        body.getMassData().mass = 60;
    }

    @Override
    protected void addedToEngine(final Engine engine) {
        subscribe(Subscriptions.removeOnDeathSubscription(this));
    }
}
