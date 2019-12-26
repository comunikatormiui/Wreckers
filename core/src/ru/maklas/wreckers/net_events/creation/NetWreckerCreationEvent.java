package ru.maklas.wreckers.net_events.creation;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.HealthComponent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.utils.net_dispatcher.NetEvent;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class NetWreckerCreationEvent implements NetEvent {

	int id;
	float x;
	float y;
	float angle;
	float health;
	WreckerComponent stats;
	boolean player;

	public NetWreckerCreationEvent(int id, float x, float y, float angle, WreckerComponent stats, float health, boolean player) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.stats = stats;
		this.health = health;
		this.player = player;
	}

	public NetWreckerCreationEvent() {

	}

	public NetWreckerCreationEvent setAndRet(int id, float x, float y, float angle, WreckerComponent stats, float health, boolean player) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.stats = stats;
		this.health = health;
		this.player = player;
		return this;
	}

	@Nullable
	public static NetWreckerCreationEvent fromEntity(Entity e, boolean isPlayer){
		WreckerComponent wc = e.get(M.wrecker);
		HealthComponent hc = e.get(M.health);
		if (wc == null || hc == null){
			return null;
		}
		return new NetWreckerCreationEvent(e.id, e.x, e.y, 0, (WreckerComponent) wc.copy(), hc.health, isPlayer);
	}

	public int getId() {
		return this.id;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getAngle() {
		return this.angle;
	}

	public WreckerComponent getStats() {
		return this.stats;
	}

	public float getHealth() {
		return health;
	}

	/**
	 * True = player,
	 * False = opponent
	 */
	public boolean isPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return "NetWreckerCreationEvent{" +
		"id=" + id +
		", x=" + x +
		", y=" + y +
		", angle=" + angle +
		", stats=" + stats +
		", player=" + player +
		'}';
	}
}
