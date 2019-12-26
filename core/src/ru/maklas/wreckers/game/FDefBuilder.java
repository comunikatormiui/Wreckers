package ru.maklas.wreckers.game;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import ru.maklas.wreckers.statics.EntityType;

public class FDefBuilder {

	private FixtureDef fDef = new FixtureDef();

	public FDefBuilder() {

	}

	public FDefBuilder newFixture(){
		fDef = new FixtureDef();
		return this;
	}

	public FDefBuilder setSensor(){
		fDef.isSensor = true;
		return this;
	}

	public FDefBuilder mask(int category, int collisionMask){
		fDef.filter.categoryBits = (short) category;
		fDef.filter.maskBits = (short) collisionMask;
		return this;
	}

	public FDefBuilder mask(EntityType masks){
		fDef.filter.categoryBits = masks.category;
		fDef.filter.maskBits = masks.mask;
		return this;
	}

	public FDefBuilder shape(Shape shape){
		fDef.shape = shape;
		return this;
	}

	/**
	 * Определяет массу на квадратный метр объекта
	 */
	public FDefBuilder density(float density){
		fDef.density = density;
		return this;
	}

	/**
	 * По дефолту 0.2f
	 */
	public FDefBuilder friction(float friction){
		fDef.friction = friction;
		return this;
	}

	public FDefBuilder bounciness(float b){
		fDef.restitution = b;
		return this;
	}

	public FixtureDef build(){
		return fDef;
	}
}
