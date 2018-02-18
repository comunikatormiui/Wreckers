package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.AntiGravComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.libs.Utils;

public class AntiGravSystem extends EntitySystem {

    ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(AntiGravComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity player : entities) {
            AntiGravComponent antiGrav = player.get(Mappers.antiGravM);
            if (!antiGrav.enabled){
                continue;
            }

            if (antiGrav.directionUp){
                antiGrav.deltaY += antiGrav.speed * dt;
                if (antiGrav.deltaY > antiGrav.maxY){
                    antiGrav.directionUp = false;
                }
            } else {
                antiGrav.deltaY -= antiGrav.speed * dt;
                if (antiGrav.deltaY < -antiGrav.maxY){
                    antiGrav.directionUp = true;
                }
            }

            PhysicsComponent pc = player.get(Mappers.physicsM);
            if (pc != null){
                pc.body.applyForceToCenter(Utils.vec1.set(0, (9.8f * antiGrav.mass) + antiGrav.deltaY), true);
            }
        }
    }

    @Override
    public void removeFromEngine() {
        super.removeFromEngine();
    }
}
