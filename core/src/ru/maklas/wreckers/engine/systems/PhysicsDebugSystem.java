package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.libs.Utils;

public class PhysicsDebugSystem extends EntitySystem {

    private final OrthographicCamera worldCamera;
    private final float scale;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeR = new ShapeRenderer();
    private World world;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, OrthographicCamera gameCamera, float scale){
        this.scale = scale;
        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = gameCamera;
        this.worldCamera = new OrthographicCamera(gameCamera.viewportWidth / scale, gameCamera.viewportHeight / scale);
    }

    private Array<Body> bodies = new Array<Body>();

    @Override
    public void update(float deltaTime) {
        Vector3 position = camera.position;
        worldCamera.position.set(position.x/scale, position.y/scale, position.z/scale);
        worldCamera.update();
        debugRenderer.render(world, worldCamera.combined);

        shapeR.setProjectionMatrix(worldCamera.combined);
        shapeR.begin(ShapeRenderer.ShapeType.Line);
        world.getBodies(bodies);
        for (Body body : bodies) {
            final Vector2 origin = Utils.vec1.set(body.getPosition());
            final Vector2 center = Utils.vec2.set(body.getWorldPoint(body.getMassData().center));
            shapeR.line(origin, center);
            shapeR.setColor(Color.RED);
            shapeR.circle(origin.x, origin.y, 10 / GameAssets.box2dScale);
            shapeR.setColor(Color.GREEN);
            shapeR.circle(center.x, center.y, 10 / GameAssets.box2dScale);
        }

        shapeR.end();

    }
}
