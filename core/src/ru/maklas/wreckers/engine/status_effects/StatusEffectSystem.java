package ru.maklas.wreckers.engine.status_effects;

import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.*;
import ru.maklas.wreckers.engine.M;

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
		ComponentMapper<StatusEffectComponent> effectM = M.effect;

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
		StatusEffectComponent sec = entity.get(M.effect);
		if (sec != null){
			sec.removeAll();
		}
	}
}
