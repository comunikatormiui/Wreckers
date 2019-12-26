package ru.maklas.wreckers.engine.status_effects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class StatusEffectComponent implements Component {

	private final Engine engine;
	private final Entity entity;
	public boolean isUpdating = false;
	private final Array<StatusEffect> effects = new Array<>(true, 5);
	private final ImmutableArray<StatusEffect> immutableEffects = new ImmutableArray<StatusEffect>(effects);
	public final Array<StatusEffect> effectUpdateArray = new Array<>(true, 5);

	public StatusEffectComponent(@NotNull Engine engine, @NotNull Entity entity) {
		this.engine = engine;
		this.entity = entity;
	}

	public boolean add(final StatusEffect effect){
		if (!effect.canBeApplied(entity, immutableEffects)){
			return false;
		}

		ImmutableArray<StatusEffect> immutableEffects = new ImmutableArray<StatusEffect>(effects);
		effect.inject(engine, entity, this, immutableEffects);
		for (StatusEffect statusEffect : effects) {
			statusEffect.otherEffectAdded(effect, immutableEffects);
		}
		effects.add(effect);
		effects.sort();
		effect.applied();
		if (!isUpdating){
			effectUpdateArray.add(effect);
			effectUpdateArray.sort();
		} else {
			engine.executeAfterUpdate(new Runnable() {
				@Override
				public void run() {
					effectUpdateArray.add(effect);
					effectUpdateArray.sort();
				}
			});
		}
		return true;
	}

	public void remove(final StatusEffect effect){
		if (effects.removeValue(effect, true)){

			for (StatusEffect statusEffect : effects) {
				statusEffect.otherEffectRemoved(effect, immutableEffects);
			}

			effect.removed();

			if (!isUpdating){
				effectUpdateArray.removeValue(effect, true);
			} else {
				engine.executeAfterUpdate(new Runnable() {
					@Override
					public void run() {
						effectUpdateArray.removeValue(effect, true);
					}
				});
			}
		}
	}

	public void removeAll() {
		if (effects.size == 0){
			return;
		}

		StatusEffect[] effectsArray = effects.toArray(StatusEffect.class);
		effects.clear();
		if (isUpdating){
			engine.executeAfterUpdate(new Runnable() {
				@Override
				public void run() {
					effectUpdateArray.clear();
				}
			});
		} else {
			effectUpdateArray.clear();
		}

		for (StatusEffect statusEffect : effectsArray) {
			statusEffect.removed();
		}

	}

	public boolean contains(Class<DisarmStatusEffect> disarmStatusEffectClass) {
		for (StatusEffect effect : effects) {
			if (effect.getClass() == disarmStatusEffectClass){
				return true;
			}
		}
		return false;
	}
}
