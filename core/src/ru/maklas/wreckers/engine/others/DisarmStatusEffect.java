package ru.maklas.wreckers.engine.others;

import ru.maklas.mengine.Entity;
import com.badlogic.gdx.utils.ImmutableArray;

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
