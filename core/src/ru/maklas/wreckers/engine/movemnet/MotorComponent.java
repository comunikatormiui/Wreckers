package ru.maklas.wreckers.engine.movemnet;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Component;

public class MotorComponent implements Component {

	public final Vector2 direction = new Vector2(0, 0);
	public float maxVelocity = 0;
	public boolean enabled = true;

	public MotorComponent(float maxVel, float x, float y) {
		direction.set(x, y);
		this.maxVelocity = maxVel;
	}

	public MotorComponent(float maxVel) {
		this.maxVelocity = maxVel;
	}
}
