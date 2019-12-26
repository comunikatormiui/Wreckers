package ru.maklas.wreckers.engine.status_effects;

import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.movemnet.AntiGravComponent;
import ru.maklas.wreckers.engine.movemnet.MotorComponent;

public class StunEffect extends TimedEffect {

	public StunEffect(float seconds) {
		super(0, seconds);
	}

	@Override
	public boolean canBeApplied(Entity e, ImmutableArray<StatusEffect> effects) {
		for (StatusEffect statusEffect : effects) {
			if (statusEffect instanceof StunEffect){
				return false;
			}
		}
		return true;
	}

	@Override
	public void applied() {
		AntiGravComponent antiGrav = owner.get(M.antiGrav);
		if (antiGrav != null) {
			antiGrav.antiGravEnabled = false;
		}
		MotorComponent mc = owner.get(M.motor);
		if (mc != null) {
			mc.enabled = false;
		}
	}

	@Override
	public void removed() {
		AntiGravComponent antiGrav = owner.get(M.antiGrav);
		if (antiGrav != null) {
			antiGrav.antiGravEnabled = true;
		}
		MotorComponent mc = owner.get(M.motor);
		if (mc != null) {
			mc.enabled = true;
		}
	}
}
