package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.components.VelocityComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.systems.MovementSystem;
import ru.maklas.wreckers.engine.systems.RenderingSystem;
import ru.maklas.wreckers.libs.gsm_lib.State;

public class MainMenuState extends State {

    Engine engine;
    Entity ball;

    @Override
    protected void onCreate() {
        Images.load();
        OrthographicCamera camera = new OrthographicCamera(720, 1280);
        engine = new Engine();
        engine.add(new MovementSystem());
        engine.add(new RenderingSystem(batch, camera));



        ball = new Entity();
        ball.x = 100;
        ball.y = 200;
        ball.add(new VelocityComponent(25, 0));
        ball.add(new RenderComponent(Images.point));

        engine.add(ball);
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        engine.render();
        batch.end();
    }

    @Override
    protected void dispose() {

    }
}
