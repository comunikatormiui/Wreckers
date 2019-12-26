package ru.maklas.wreckers.statics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

public class Game {

	public static final float gravitationalAcceleration = 9.8f;
	public static final float scale = 40;
	public static final float scaleReversed = 1.0f / scale;

	public static final float width = 720;
	public static float height = 1280;
	public static final float hWidth = width / 2;
	public static float hHeight = height/2;



	public static void getEntityDirection(float angle, Vector2 direction) {
		direction.set(1, 0).setAngle(angle);
	}


	public static void setFilterData(@NotNull Body body, boolean includingSensors, EntityType type) {
		Array<Fixture> fixtureList = body.getFixtureList();
		for (Fixture fixture : fixtureList) {
			if (fixture.isSensor() && !includingSensors) {
				continue;
			}
			Filter filterData = fixture.getFilterData();
			filterData.categoryBits = type.category;
			filterData.maskBits	 = type.mask;
			fixture.setFilterData(filterData);
		}
	}

	/**
	 * Возвращает процент проходимого урона после учёта резистов. Применяется так:
	 * <p>уронСУчётомРезистов = чистыйУрон * leagueFormula(резист);</p>
	 * 300 -> 25% от чистого урона
	 */
	public static float leagueFormula(float resist){
		if (resist < -50){
			resist = -50;
		}
		return ((100) / (100 + resist));
	}

}
