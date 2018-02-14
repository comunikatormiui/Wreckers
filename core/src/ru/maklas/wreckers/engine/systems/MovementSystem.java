package ru.maklas.wreckers.engine.systems;


import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.AccelerationComponent;
import ru.maklas.wreckers.engine.components.VelocityComponent;

/**
 * Created by Danil on 15.08.2017.
 *
 */

public class MovementSystem extends EntitySystem {


    private ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(VelocityComponent.class);
    }

    public void update(float deltaTime) {
        ComponentMapper<VelocityComponent> velocityM = Mappers.velocityM;
        ComponentMapper<AccelerationComponent> accelerationM = Mappers.accelerationM;
        ImmutableArray<Entity> entities = this.entities;


        for (int i = 0; i < entities.size(); i++) {

            Entity entity = entities.get(i);

            VelocityComponent velocity = entity.get(velocityM);
            AccelerationComponent acceleration = entity.get(accelerationM);

            // увеличиваем скорость
            if (acceleration != null) {
                velocity.x += acceleration.x * deltaTime;
                velocity.y += acceleration.y * deltaTime;
            }

            // меняем позицию
            entity.x += velocity.x * deltaTime;
            entity.y += velocity.y * deltaTime;

        }
    }

}
