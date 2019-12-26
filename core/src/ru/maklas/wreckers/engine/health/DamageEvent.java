package ru.maklas.wreckers.engine.health;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Event;

public class DamageEvent {

	Entity target;
	float damage;
	@Nullable
	Event hitEvent;

	public DamageEvent(Entity target, float damage, @Nullable Event hitEvent) {
		this.target = target;
		this.damage = damage;
		this.hitEvent = hitEvent;
	}

	public Entity getTarget() {
		return target;
	}

	public float getDamage() {
		return damage;
	}

	@Nullable
	public Event getHitEvent() {
		return hitEvent;
	}
}
