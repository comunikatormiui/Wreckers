package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.StringUtils;

public class PhysicsComponent implements Component{

	public Body body;

	public PhysicsComponent(Body body) {
		this.body = body;
	}

	public void updateEntity(Entity e){
		Vector2 bodyPos = body.getPosition();
		e.x = bodyPos.x * Game.scale;
		e.y = bodyPos.y * Game.scale;
		e.setAngle(body.getAngle() * MathUtils.radiansToDegrees);
	}

	@Override
	public String toString() {
		if (body == null)
			return "Physics.NULL";
		else
			return "Physics{" +
					"t=" + body.getType().name().replace("Body", "") +
					", m=" + StringUtils.ff(body.getMass()) +
					", v=" + StringUtils.vec(body.getLinearVelocity().scl(Game.scale), 1) +
					'}';
	}

}
