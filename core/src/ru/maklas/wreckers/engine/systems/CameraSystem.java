package ru.maklas.wreckers.engine.systems;


import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.CameraComponent;

/**
 * Created by Danil on 27.10.2017.
 */

public class CameraSystem extends EntitySystem {

    private ComponentMapper<CameraComponent> cameraM = Mappers.cameraM;
    private ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = entitiesFor(CameraComponent.class);
    }



    @Override
    public void update(float deltaTime) {
        for (Entity e : entities) {

            CameraComponent cc = e.get(cameraM);

            cc.cam.position.set(e.x, e.y, 0);
            cc.cam.update();
        }

    }
}
