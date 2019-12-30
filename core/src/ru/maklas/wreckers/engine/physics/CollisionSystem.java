package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.SubscriptionSystem;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;

public class CollisionSystem extends SubscriptionSystem {




	/**
	 * @param collisionVelocity Velocity of collision of Wrecker and Weapon
	 * @param box2dPlayerNormal
	 * @return 0...1 where 1 means dull and 0 is slicing
	 */
	public static float calculateDullness(Vector2 collisionVelocity, Vector2 box2dPlayerNormal) {
		float angle = collisionVelocity.angle(box2dPlayerNormal);
		angle = angle < 0 ? -angle : angle; // abs(angle) 0..180
		angle -= 90; // -90..90
		angle = angle < 0 ? -angle : angle; //0..90 где 0 - абсолютно режущий удар, а 90 - абсолютно тупой
		return angle / 90f;
	}

	public static float dullnessLogisticFunction(float x) {
		return (float) (1 / (1 + Math.exp(-(x - 0.3) / 0.05)));
	}

	public static float sharpnessLogisticFunction(float x) {
		return (float) (1 / (1 + Math.exp(-(x - 0.7) / 0.05)));
	}


	/**
	 * @param weaponFD Тип Fixture оружия - проникающий
	 * @param weaponFixture сама Fixture
	 * @return 0..1 Sharpness
	 */
	public static float calculateSharpness(@NotNull WeaponPiercingFD weaponFD, Fixture weaponFixture, Vector2 collisionVelocity) {
		Vector2 worldPierceDirection = new Vector2();
		weaponFD.getWorldDirection(weaponFixture, worldPierceDirection);
		float angle = worldPierceDirection.angle(collisionVelocity); // будут ценится значения ~0 и ~180
		angle = angle < 0 ? -angle : angle; // берем модуль 0..180
		angle -= 90; // -90..90 Ценятся ~-90 и ~90
		angle = angle < 0 ? -angle : angle; // снова берем модуль 0..90. где 90 - топ
		angle /= 90f; //0..1
		return angle > 0.6f ? angle : 0; //Отсеиваем тупые удары
	}

	/**
	 * @param playerFixture Игрок
	 * @param box2dCollisionPoint точка прикосновения в формате box2d
	 * @param normal Нормаль прикосновения
	 * @param weaponIsA Оружие - А в изначальном контакте
	 * @return Вектор нормали от игрока к точке
	 */
	public static Vector2 calculatePlayerNormal(Fixture playerFixture, Vector2 box2dCollisionPoint, Vector2 normal, boolean weaponIsA) {
		if (playerFixture.getShape() instanceof CircleShape) { //Если игрок - круг, то вектор нормали всегда исходит от центра к точке.
			return new Vector2(box2dCollisionPoint).sub(playerFixture.getBody().getPosition()).nor();
		} else {
			Vector2 n = new Vector2(normal);
			return weaponIsA ? n.scl(-1) : n;
		}
	}

}
