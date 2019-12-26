package ru.maklas.wreckers.engine.health;

import ru.maklas.mengine.Component;

public class HealthComponent implements Component{

	public float maxHealth;
	public float health;
	public boolean alive = true;
	public long lastDamageDone;
	public float regeneration = 1f; //per second

	public HealthComponent(float maxHealth) {
		this.maxHealth = this.health = maxHealth;
	}

	public float getHealthPercent(){
		return health / maxHealth;
	}

	public boolean isFull(){
		return health >= maxHealth;
	}

}
