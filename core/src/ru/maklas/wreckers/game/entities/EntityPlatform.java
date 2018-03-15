package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.FixtureData;

public class EntityPlatform extends ru.maklas.wreckers.game.entities.GameEntity {


    public EntityPlatform(int id, float x, float y, int zOrder, float width, float height, GameModel model) {
        super(id, EntityType.OBSTACLE, x, y, zOrder);

        Body platformBody = model.getBuilder()
                .newBody()
                .addFixture(model.getFixturer().newFixture()
                        .shape(model.getShaper().buildRectangle(0, 0, width, height))
                        .friction(0.1f)
                        .density(10)
                        .bounciness(0.2f)
                        .mask(EntityType.OBSTACLE)
                        .build(), new FixtureData(FixtureType.OBSTACLE))
                .pos(-360, 200)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        add(new PhysicsComponent(platformBody));
    }
}
