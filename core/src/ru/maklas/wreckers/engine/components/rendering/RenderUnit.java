package ru.maklas.wreckers.engine.components.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author maklas. Created on 01.05.2017.
 */

public class RenderUnit {

    public float localX;
    public float localY;
    public float width;
    public float height;
    public float pivotX = 0.5f;
    public float pivotY = 0.5f;
    public float angle;
    public TextureRegion region;
    public float scaleX = 1f;
    public float scaleY = 1;
    public boolean flipX = false;

    public String name;

    public RenderUnit() {

    }

    public RenderUnit(TextureRegion region) {
        this.region = region;
        width = region.getRegionWidth();
        height = region.getRegionHeight();
    }



}
