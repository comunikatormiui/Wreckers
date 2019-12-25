package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.MotorComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;

/**
 * Отвечает за передвижение игрока, придавая постоянное ускорение.
 */
public class MotorSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private Vector2 tempVec = new Vector2();

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(MotorComponent.class);
    }

    @Override
    public void update(float dt) {
        Vector2 tempVec = this.tempVec;
        for (Entity entity : entities) {
            MotorComponent mc = entity.get(Mappers.motorM);
            if (!mc.enabled){
                continue;
            }
            PhysicsComponent pc = entity.get(Mappers.physicsM);
            if (pc != null){
                tempVec.set(mc.direction).clamp(0, 1).scl(mc.maxVelocity * pc.body.getMass());
                pc.body.applyForceToCenter(tempVec, true);
            }
        }
    }

}
