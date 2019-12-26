package ru.maklas.wreckers.engine.movemnet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.weapon.AttachEvent;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.Utils;

/**
 * <p>
 *	 Работает с AntiGravComponent. Обновляет массу Entity при её изменении.
 *	 При включенном режиме, добавляет Entity вертикальное направление противоположное гравитации
 *	 для поддержания стабильной высоты. Так же вращает Entity в нужном направлении чтобы Entity стремился
 *	 к вертикальному положению в пространстве. Иногда пошатывает Entity чтобы выглядело аутентично.
 * </p>
 */
public class AntiGravSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	@Override
	public void onAddedToEngine(Engine engine) {
		entities = engine.entitiesFor(AntiGravComponent.class);
		subscribe(AttachEvent.class, this::onAttachEvent);
	}

	private void onAttachEvent(AttachEvent e) {
		AntiGravComponent antiGrav = e.getOwner().get(M.antiGrav);
		ComponentMapper<PhysicsComponent> physicsM = M.physics;
		PhysicsComponent weaponPC = e.getAttachable().get(physicsM);

		if (antiGrav == null || weaponPC == null) {
			return;
		}

		if (e.isAttached()) {
			antiGrav.mass += weaponPC.body.getMass();
		} else {
			antiGrav.mass -= weaponPC.body.getMass();
		}
	}

	@Override
	public void update(float dt) {
		for (Entity player : entities) {
			AntiGravComponent antiGrav = player.get(M.antiGrav);
			if (!antiGrav.antiGravEnabled){
				continue;
			}

			PhysicsComponent pc = player.get(M.physics);
			if (pc != null){
				Vector2 force = Utils.vec1;
				force.set(0, Game.gravitationalAcceleration * antiGrav.mass);
				if (antiGrav.randomMovementEnabled){
					changeDeltas(antiGrav, dt);
					force.add(antiGrav.dX, antiGrav.dY);
				}
				pc.body.applyForceToCenter(force, true);
			}
		}
	}


	void changeDeltas(AntiGravComponent antiGrav, float dt){
		if (antiGrav.directionUp){
			antiGrav.dY += antiGrav.changeSpeed * dt;

			if (antiGrav.dY > antiGrav.maxY){
				antiGrav.directionUp = false;
			}
		} else {
			antiGrav.dY -= antiGrav.changeSpeed * dt;

			if (antiGrav.dY < -antiGrav.maxY){
				antiGrav.directionUp = true;
			}
		}

		if (antiGrav.directionRight){
			antiGrav.dX += antiGrav.changeSpeed * dt;

			if (antiGrav.dX > antiGrav.maxX){
				antiGrav.directionRight = false;
			}
		} else {
			antiGrav.dX -= antiGrav.changeSpeed * dt;

			if (antiGrav.dX < -antiGrav.maxX){
				antiGrav.directionRight = true;
			}
		}
	}
}
