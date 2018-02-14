package ru.maklas.wreckers.engine.components.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.maklas.mengine.Entity;

/**
 * Created by maklas on 04-Jan-18.
 */

public class AnimationTrigger extends Animation {

    public int triggerFrame;
    public Action triggerAction;

    public AnimationTrigger(TextureRegion[] regions, RenderUnit ru, float cycleTime) {
        super(regions, ru, cycleTime);
    }



    public interface Action{

        void execute(Entity entity, AnimationTrigger animation);

    }

}
