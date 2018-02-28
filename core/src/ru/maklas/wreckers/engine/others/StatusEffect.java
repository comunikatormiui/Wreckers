package ru.maklas.wreckers.engine.others;

import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.engine.components.StatusEffectComponent;

public abstract class StatusEffect implements Comparable<StatusEffect>{

    protected final int priority;
    protected Engine engine;
    protected Entity owner;
    protected ImmutableArray<StatusEffect> currentEffects;
    private StatusEffectComponent statusEffectComponent;

    protected StatusEffect(int priority) {
        this.priority = priority;
    }

    public StatusEffect() {
        this(0);
    }

    /**
     * Может ли этот эффект быть применён к данному Entity
     * @param effects - Массив текущих применённых эффектов на этом Entity
     */
    public abstract boolean canBeApplied(Entity e, ImmutableArray<StatusEffect> effects);

    public final void inject(Engine engine, Entity entity, StatusEffectComponent statusEffectComponent, ImmutableArray<StatusEffect> immutableEffects) {
        this.engine = engine;
        this.owner = entity;
        this.statusEffectComponent = statusEffectComponent;
        this.currentEffects = immutableEffects;
    }

    protected final void removeSelf(){
        statusEffectComponent.remove(this);
    }

    public void otherEffectAdded(StatusEffect effect, ImmutableArray<StatusEffect> effectListWithoutNew){}

    public void otherEffectRemoved(StatusEffect effect, ImmutableArray<StatusEffect> effectListWithoutOld){}

    public abstract void applied();

    public void update(float dt){}

    public abstract void removed();

    @Override
    public final int compareTo(@NotNull StatusEffect otherEffect) {
        return priority - otherEffect.priority;
    }
}
