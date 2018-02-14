package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Danil on 27.10.2017.
 */

public class CameraComponent implements Component, Pool.Poolable{

    public OrthographicCamera cam;

    public CameraComponent(OrthographicCamera cam) {
        this.cam = cam;
    }

    public CameraComponent setUp(OrthographicCamera cam) {
        this.cam = cam;
        return this;
    }

    public CameraComponent() {

    }

    @Override
    public void reset() {
        cam = null;
    }

    @Override
    public String toString() {
        return "CameraComponent{" +
                + cam.position.x +
                ", " + cam.position.y +
                '}';
    }
}
