package ru.maklas.wreckers.engine.systems;


import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.rendering.Animation;
import ru.maklas.wreckers.engine.components.rendering.AnimationComponent;
import ru.maklas.wreckers.engine.components.rendering.AnimationTrigger;

/**
 * Created by Danil on 19.08.2017.
 */

public class AnimationSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private final Array<AnimationCacher> delayedActions = new Array<AnimationCacher>();

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(AnimationComponent.class);
    }

    @Override
    public void update(float deltaTime) {

        Array<AnimationCacher> delayedActions = this.delayedActions;
        delayedActions.clear();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            final AnimationComponent animationComponent = entity.get(Mappers.animationM);
            final Array<Animation> animations = animationComponent.animations;
            for (Animation animation : animations) {
                if (animation.enabled) {
                    if (animation instanceof AnimationTrigger){
                        doAnimationWithTrigger(delayedActions, entity, (AnimationTrigger) animation, deltaTime);
                    } else {
                        doAnimation(animation, deltaTime);
                    }
                } else {
                    animation.ru.region = animation.defaultFrame;
                }
            }
        }

        for (AnimationCacher delayedAction : delayedActions) {
            delayedAction.execute();
        }

    }

    private void doAnimation(Animation animation, float dt) {
        final float tpf = animation.tpf;
        animation.time += dt;
        if (animation.time >= tpf){
            animation.time -= tpf;
            animation.currentFrame++;
            checkFrameOverflow(animation);
        }
        animation.ru.region = animation.frames[animation.currentFrame];
    }

    private void doAnimationWithTrigger(Array<AnimationCacher> cacher, Entity entity, AnimationTrigger animation, float dt) {
        final float tpf = animation.tpf;
        animation.time += dt;
        if (animation.time >= tpf){
            animation.time -= tpf;
            animation.currentFrame++;
            checkFrameOverflow(animation);
            if (animation.triggerFrame == animation.currentFrame && animation.triggerAction != null){
                cacher.add(new AnimationCacher(animation, entity));
            }
        }
        animation.ru.region = animation.frames[animation.currentFrame];
    }

    private void checkFrameOverflow(Animation animation) {
        if (animation.currentFrame == animation.frames.length){
            if (!animation.looped){
                animation.enabled = false;
                animation.time = 0;
            }
            animation.currentFrame = 0;
        }
    }


    private class AnimationCacher{

        AnimationTrigger anim;
        Entity entity;

        public AnimationCacher(AnimationTrigger anim, Entity entity) {
            this.anim = anim;
            this.entity = entity;
        }

        public void execute(){
            anim.triggerAction.execute(entity, anim);
        }
    }
}
