package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PlayerComponent;
import ru.maklas.wreckers.libs.Utils;

public class PlayerSystem extends EntitySystem {

    ImmutableArray<Entity> players;

    @Override
    public void onAddedToEngine(Engine engine) {
        players = engine.entitiesFor(PlayerComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity player : players) {
            PlayerComponent playerComponent = player.get(Mappers.playerM);

            if (playerComponent.directionUp){
                playerComponent.deltaY += playerComponent.speed * dt;
                if (playerComponent.deltaY > playerComponent.maxY){
                    playerComponent.directionUp = false;
                }
            } else {
                playerComponent.deltaY -= playerComponent.speed * dt;
                if (playerComponent.deltaY < -playerComponent.maxY){
                    playerComponent.directionUp = true;
                }
            }

            PhysicsComponent pc = player.get(Mappers.physicsM);
            if (pc != null){
                pc.body.applyForceToCenter(Utils.vec1.set(0, 9.8f + playerComponent.deltaY), true);
            }
        }

    }

    @Override
    public void removeFromEngine() {
        super.removeFromEngine();
    }
}
