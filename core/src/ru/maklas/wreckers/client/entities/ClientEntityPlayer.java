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
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.game.*;

import java.util.HashSet;
import java.util.Set;

public class ClientEntityPlayer extends Entity {

    private Body body;

    public ClientEntityPlayer(int id, float x, float y, float health, ClientGameModel model) {
        super(x, y, GameAssets.playerZ);
        this.id = id;
        EntityType eType = EntityType.PLAYER;
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

        body = builder.newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(5)
                .addFixture(bodyF)
                .addFixture(leftArmF)
                .addFixture(rightArmF)
                .build();

        add(new PhysicsComponent(body));
        add(new HealthComponent(health));
        add(new ShooterComponent(14, -22));
        Weapon noWeapon = WeaponAssets.createNew(WeaponType.NONE, 1, 0);
        Set<Weapon> weapons = new HashSet<Weapon>();
        weapons.add(noWeapon);
        add(new PlayerInventoryComponent(new Bag(weapons), noWeapon));
        add(new PlayerComponent());
        add(new VelocityComponent(20));
        body.getMassData().mass = 80f;
    }

    @Override
    protected void addedToEngine(Engine engine) {
        subscribe(Subscriptions.removeOnDeathSubscription(this));
    }
}
