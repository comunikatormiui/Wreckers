package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.TTLComponent;

public class TTLSystem extends EntitySystem{

    ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(TTLComponent.class);
    }

    @Override
    public void update(float dt) {
        ComponentMapper<TTLComponent> ttlM = Mappers.ttlM;
        for (Entity entity : entities) {
            TTLComponent ttlComponent = entity.get(ttlM);
            ttlComponent.ttl -= dt;
            if (ttlComponent.ttl <= 0){
                getEngine().remove(entity);
            }
        }

    }
}
