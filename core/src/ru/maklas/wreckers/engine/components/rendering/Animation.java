package ru.maklas.wreckers.engine.components.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author maklas. Created on 27.04.2017.
 */

public class Animation {

    //current time
    public float time = 0;
    //time per frame/duration
    public float tpf = 0.5f;
    public int currentFrame = 0;
    public TextureRegion[] frames;
    public TextureRegion defaultFrame;
    public RenderUnit ru;
    public boolean enabled = true;
    public boolean looped = true;

    public Animation(TextureRegion[] regions, RenderUnit ru, float cycleTime){
        this.defaultFrame = regions[0];
        ru.region = defaultFrame;
        this.frames = regions;
        this.ru = ru;
        tpf = cycleTime/regions.length;
        if (tpf < 0.016666668f){
            tpf = 0.017f;
        }
    }

    public void reset(RenderUnit ru, int frame){
        if (frame >= 0 && frame < frames.length) {
            currentFrame = frame;
            time = frame;
            ru.region = frames[frame];
        }
    }

    public void reset(RenderUnit ru){
        reset(ru, 0);
    }

    public void reset(int frame){
        ru.region = frames[frame % frames.length];
    }


}
