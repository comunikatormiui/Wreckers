package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.systems.CollisionEntitySystem;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.components.CollisionComponent;
import ru.maklas.wreckers.engine.components.VelocityComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.engine.systems.MovementSystem;
import ru.maklas.wreckers.engine.systems.PhysicsDebugSystem;
import ru.maklas.wreckers.engine.systems.PhysicsSystem;
import ru.maklas.wreckers.engine.systems.RenderingSystem;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.FixtureBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;
import ru.maklas.wreckers.libs.gsm_lib.State;

import java.awt.*;

public class MainMenuState extends State {

    Engine engine;
    Entity ball;
    World world;
    PhysicsDebugSystem debugSystem;
    OrthographicCamera cam;

    @Override
    protected void onCreate() {
        Images.load();

        cam = new OrthographicCamera(720, 1280);
        System.out.println(cam.position);
        engine = new Engine();
        world = new World(new Vector2(0, -9.8f), true);
        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);



        BodyBuilder builder = new BodyBuilder(world, GameAssets.box2dScale);
        ShapeBuilder shaper = new ShapeBuilder(GameAssets.box2dScale);
        FixtureBuilder fix = new FixtureBuilder(world, shaper);


        Body ballBody = builder.newBody()
                .addFixture(fix.buildCircle(0, 0, 15,  1,  1))
                .pos(0, 100)
                .vel(0, 100)
                .linearDamp(0)
                .type(BodyDef.BodyType.DynamicBody)
                .build();

        Body platformBody = builder.newBody()
                .addFixture(fix.buildRectangle(0, 0, 720, 10, 100, 1, 1))
                .pos(-360, 0)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        ball = new Entity();
        ball.x = 0;
        ball.y = 100;
        ball.add(new CollisionComponent(ballBody));
        RenderUnit unit = new RenderUnit(Images.point);
        unit.pivotX = 0.5f;
        unit.pivotY = 0.5f;
        ball.add(new RenderComponent(unit));

        Entity platform = new Entity();
        platform.add(new CollisionComponent(platformBody));
        engine.add(ball);
        engine.add(platform);
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        System.out.println(ball);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        engine.render();
        batch.end();
        debugSystem.update(0);
    }

    @Override
    protected void dispose() {

    }
}
