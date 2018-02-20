package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntityListener;
import ru.maklas.mengine.systems.CollisionEntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;

public class PhysicsSystem extends CollisionEntitySystem implements EntityListener {

    private final World world;
    private ImmutableArray<Entity> entities;

    public PhysicsSystem(World world) {
        this.world = world;
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        entities = engine.entitiesFor(PhysicsComponent.class);
        for (Entity entity : entities) {
            entity.get(Mappers.physicsM).body.setUserData(entity);
        }
        engine.addListener(this);
    }


    @Override
    public void update(float dt) {
        World world = this.world;
        float scale = GameAssets.box2dScale;

        world.step(0.016666667f, 6, 2);

        ComponentMapper<PhysicsComponent> collisionM = Mappers.physicsM;
        for (Entity entity : entities) {
            PhysicsComponent cc = entity.get(collisionM);

            Vector2 bodyPos = cc.body.getPosition();
            entity.x = bodyPos.x * scale;
            entity.y = bodyPos.y * scale;
            entity.setAngle(cc.body.getAngle() * MathUtils.radiansToDegrees);

        }

    }

    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent cc = entity.get(Mappers.physicsM);
        if (cc != null) {
            cc.body.setUserData(entity);
            validateFixtureData(entity, cc.body);
        }
    }

    private void validateFixtureData(Entity entity, Body body){
        Array<Fixture> fixtureList = body.getFixtureList();
        for (Fixture fixture : fixtureList) {
            if (fixture.getUserData() == null){
                throw new RuntimeException(entity.getClass().getSimpleName() + " -- " + entity.toString() + " has fixtures without user data!");
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent cc = entity.get(Mappers.physicsM);
        if (cc != null) {
            world.destroyBody(cc.body);
        }
    }
}
