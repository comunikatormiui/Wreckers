package ru.maklas.wreckers.engine.others;

import ru.maklas.mengine.Entity;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.AntiGravComponent;
import ru.maklas.wreckers.engine.components.MotorComponent;

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
        AntiGravComponent antiGrav = owner.get(Mappers.antiGravM);
        if (antiGrav != null) {
            antiGrav.antiGravEnabled = false;
        }
        MotorComponent mc = owner.get(Mappers.motorM);
        if (mc != null) {
            mc.enabled = false;
        }
    }

    @Override
    public void removed() {
        AntiGravComponent antiGrav = owner.get(Mappers.antiGravM);
        if (antiGrav != null) {
            antiGrav.antiGravEnabled = true;
        }
        MotorComponent mc = owner.get(Mappers.motorM);
        if (mc != null) {
            mc.enabled = true;
        }
    }
}
