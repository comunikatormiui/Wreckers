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


    }
}
