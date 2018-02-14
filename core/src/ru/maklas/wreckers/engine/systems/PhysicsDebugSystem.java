package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.EntitySystem;

public class PhysicsDebugSystem extends EntitySystem {

    private final OrthographicCamera worldCamera;
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, OrthographicCamera gameCamera){
        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = gameCamera;
        this.worldCamera = new OrthographicCamera(gameCamera.viewportWidth, gameCamera.viewportHeight);
    }

    @Override
    public void update(float deltaTime) {
        worldCamera.position.set(camera.position);
        worldCamera.update();
        debugRenderer.render(world, worldCamera.combined);
    }
}
