package ru.maklas.wreckers.engine.wrecker;

import com.badlogic.gdx.physics.box2d.Joint;
import ru.maklas.mengine.Entity;

/** Сокет с оружием */
public class WSocket {

	public Entity attachedEntity;
	public Joint joint; //joint соеденяющий с прикрепленным предметом
	public float localX;
	public float localY;

	public boolean isEmpty(){
		return attachedEntity == null;
	}

}
