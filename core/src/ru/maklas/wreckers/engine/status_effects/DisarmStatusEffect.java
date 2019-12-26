package ru.maklas.wreckers.engine.status_effects;

import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Entity;

public class DisarmStatusEffect extends TimedEffect {


	public DisarmStatusEffect(float seconds) {
		super(1, seconds);
	}

	@Override
	public boolean canBeApplied(Entity e, ImmutableArray<StatusEffect> effects) {
		return true;
	}

	@Override
	public void applied() {

	}

	@Override
	public void removed() {

	}
}
