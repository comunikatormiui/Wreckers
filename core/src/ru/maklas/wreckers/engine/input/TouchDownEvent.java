package ru.maklas.wreckers.engine.input;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.wreckers.engine.other.Event;

/** Нажатие на экран **/
@Deprecated
public class TouchDownEvent implements Event {

	float x;
	float y;
	int finger;
	int button;

	public TouchDownEvent(float x, float y, int finger, int button) {
		this.x = x;
		this.y = y;
		this.finger = finger;
		this.button = button;
	}

	public TouchDownEvent(Vector2 vec, int finger, int button) {
		this.x = vec.x;
		this.y = vec.y;
		this.finger = finger;
		this.button = button;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getFinger() {
		return finger;
	}

	public void setFinger(int finger) {
		this.finger = finger;
	}

	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}
}
