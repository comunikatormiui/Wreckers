package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.*;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.StatusEffectComponent;
import ru.maklas.wreckers.engine.others.StatusEffect;

/**
 * Система эффектов. Управляет эффектами игроков, обновляет.
 */
public class StatusEffectSystem extends EntitySystem implements EntityListener {

    ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        engine.addListener(this);
        entities = engine.entitiesFor(StatusEffectComponent.class);
    }

    @Override
    public void update(float dt) {
        ComponentMapper<StatusEffectComponent> effectM = Mappers.effectM;

        for (Entity entity : entities) {
            StatusEffectComponent sec = entity.get(effectM);
            sec.isUpdating = true;
            for (StatusEffect eff : sec.effectUpdateArray) {
                eff.update(dt);
            }

            sec.isUpdating = false;
        }
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
    }


    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        StatusEffectComponent sec = entity.get(Mappers.effectM);
        if (sec != null){
            sec.removeAll();
        }
    }
}
