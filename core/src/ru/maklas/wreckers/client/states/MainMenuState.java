package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.assets.Masks;
import ru.maklas.wreckers.client.entities.EntityPlayer;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.CollisionComponent;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.ShootEvent;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.Box2dModel;
import ru.maklas.wreckers.game.FDefBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;

public class MainMenuState extends State {

    Engine engine;
    EntityPlayer player;
    World world;
    PhysicsDebugSystem debugSystem;
    OrthographicCamera cam;

    BodyBuilder builder;
    ShapeBuilder shaper;
    FDefBuilder fix;
    Box2dModel box2dModel;

    @Override
    protected void onCreate() {
        Images.load();

        cam = new OrthographicCamera(720, 1280);
        System.out.println(cam.position);
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
                CollisionEvent event = new CollisionEvent(a, b, contact, true);
                a.get(Mappers.collisionM).signal.dispatch(event);

                CollisionEvent event2 = new CollisionEvent(b, a, contact, true);
                a.get(Mappers.collisionM).signal.dispatch(event2);

                engine.dispatch(event);
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });


        builder = new BodyBuilder(world, GameAssets.box2dScale);
        shaper = new ShapeBuilder(GameAssets.box2dScale);
        fix = new FDefBuilder();

        box2dModel = new Box2dModel(world, shaper, fix, builder);

        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new HealthSystem());
        engine.add(new ShooterSystem(box2dModel));
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);


        {
            Entity entity = new Entity();
            entity.add(new HealthComponent(100));
            engine.add(entity);
            System.out.println(engine.entitiesFor(HealthComponent.class).size());

        }



        Body platformBody = builder
                .newBody()
                .addFixture(fix.newFixture()
                        .shape(shaper.buildRectangle(0, 0, 720, 100))
                        .friction(0)
                        .density(10)
                        .bounciness(1)
                        .mask(Masks.OBSTACLE)
                        .build())
                .pos(-360, 200)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        player = new EntityPlayer(1, 0, 300, 100, box2dModel, Masks.PLAYER);

        Entity platform = new Entity();
        platform.add(new CollisionComponent(platformBody));
        engine.add(player);
        engine.add(platform);
        engine.add(new EntityPlayer(2, 0, 400, 100, box2dModel, Masks.ZOMBIE));
    }

    @Override
    protected InputProcessor getInput() {
        return new InputAdapter(){
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                GameAssets.rotateBody(player.get(Mappers.collisionM).body, realMouse.x, realMouse.y);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                GameAssets.rotateBody(player.get(Mappers.collisionM).body, realMouse.x, realMouse.y);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.RIGHT){
                    return false;
                }

                engine.dispatch(new ShootEvent(player));

                if (true){
                    return true;
                }
                return true;
            }
        };
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        Vector2 realMouse = Utils.toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
        player.lookAt(realMouse.x, realMouse.y);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            player.moveForward(20);
        }
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
