package ru.maklas.wreckers.engine.movemnet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;

/** Отвечает за передвижение игрока, придавая постоянное ускорение **/
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
			MotorComponent mc = entity.get(M.motor);
			if (!mc.enabled){
				continue;
			}
			PhysicsComponent pc = entity.get(M.physics);
			if (pc != null){
				tempVec.set(mc.direction).clamp(0, 1).scl(mc.maxVelocity * pc.body.getMass());
				pc.body.applyForceToCenter(tempVec, true);
			}
		}
	}

}
