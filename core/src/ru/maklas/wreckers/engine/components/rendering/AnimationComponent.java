package ru.maklas.wreckers.engine.components.rendering;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.Component;

public class AnimationComponent implements Component, Pool.Poolable {

    public final Array<Animation> animations = new Array<Animation>(3);

    public AnimationComponent() {

    }

    public AnimationComponent(Animation animation) {
        animations.add(animation);
    }

    public AnimationComponent add(Animation animation) {
        animations.add(animation);
        return this;
    }

    public Animation findByName(String name) {
        for (Animation animation : animations) {
            if (name.equals(animation.ru.name)){
                return animation;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        animations.clear();
    }
}
