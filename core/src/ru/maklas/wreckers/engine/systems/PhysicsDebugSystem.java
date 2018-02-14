package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.EntitySystem;

public class PhysicsDebugSystem extends EntitySystem {

    private final OrthographicCamera worldCamera;
    private final float scale;
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, OrthographicCamera gameCamera, float scale){
        this.scale = scale;
        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = gameCamera;
        this.worldCamera = new OrthographicCamera(gameCamera.viewportWidth / scale, gameCamera.viewportHeight / scale);
    }

    @Override
    public void update(float deltaTime) {
        Vector3 position = camera.position;
        worldCamera.position.set(position.x/scale, position.y/scale, position.z/scale);
        worldCamera.update();
        debugRenderer.render(world, worldCamera.combined);
    }
}
