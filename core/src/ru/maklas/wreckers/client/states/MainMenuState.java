package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.client.entities.EntityNumber;
import ru.maklas.wreckers.client.entities.EntityPlayer;
import ru.maklas.wreckers.client.entities.EntitySword;
import ru.maklas.wreckers.client.entities.WeaponEntity;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.FDefBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;

public class MainMenuState extends State {

    Engine engine;
    World world;
    PhysicsDebugSystem debugSystem;
    OrthographicCamera cam;
    ClientGameModel model;

    @Override
    protected void onCreate() {
        Images.load();

        cam = new OrthographicCamera(720, 1280);
        engine = new Engine();
        world = new World(new Vector2(0, -9.8f), true);

        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new HealthSystem());
        engine.add(new TTLSystem());
        engine.add(new PlayerSystem());


        model = new ClientGameModel();
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);


        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
                CollisionEvent event = new CollisionEvent(a, b, contact, true);
                a.get(Mappers.physicsM).signal.dispatch(event);

                CollisionEvent event2 = new CollisionEvent(b, a, contact, true);
                a.get(Mappers.physicsM).signal.dispatch(event2);

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
                float v = impulse.getNormalImpulses()[0];
                if (v > 2){
                    WorldManifold worldManifold = contact.getWorldManifold();
                    engine.add(new EntityNumber(-(int) v, 1, worldManifold.getPoints()[0].x * GameAssets.box2dScale, worldManifold.getPoints()[0].y * GameAssets.box2dScale));
                    System.out.println(-(int) v);
                }
            }
        });
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);


        Body platformBody = model.getBuilder()
                .newBody()
                .addFixture(model.getFixturer().newFixture()
                        .shape(model.getShaper().buildRectangle(0, 0, 720, 100))
                        .friction(0)
                        .density(10)
                        .bounciness(0.2f)
                        .mask(EntityType.OBSTACLE)
                        .build())
                .pos(-360, 200)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        final Entity player = new EntityPlayer(1, 0, 500, 100, model, EntityType.PLAYER);
        model.setPlayer(player);

        Entity platform = new Entity();
        platform.add(new PhysicsComponent(platformBody));
        engine.add(player);
        engine.add(platform);
        engine.add(new EntityPlayer(1341, 200, 500, 100, model, EntityType.ZOMBIE));
        WeaponEntity sword = new EntitySword(123412, EntityType.PLAYER, -200, 700, 10, model);
        Body body = sword.get(Mappers.physicsM).body;
        RopeJointDef rjd = new RopeJointDef();
        rjd.bodyA = player.get(Mappers.physicsM).body;
        rjd.bodyB = body;
        rjd.localAnchorA.set(0, 0);
        rjd.localAnchorB.set(300, 178).scl(0.15f / GameAssets.box2dScale);
        world.createJoint(rjd);
        engine.add(sword);
    }

    @Override
    protected InputProcessor getInput() {
        return new InputAdapter(){
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                return true;
            }
        };
    }

    @Override
    protected void update(float dt) {
        model.getEngine().update(dt);
        cam.position.set(model.getPlayer().x, model.getPlayer().y, 0);


        Vector2 directionVec = Utils.vec2.set(0, 0);
        boolean triggered = false;
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            triggered = true;
            directionVec.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            triggered = true;
            directionVec.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            triggered = true;
            directionVec.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            triggered = true;
            directionVec.add(1, 0);
        }

        if (triggered){
            float velocity = model.getPlayer().get(Mappers.velocityM).velocity;
            directionVec.scl(velocity);
            model.getPlayer().get(Mappers.physicsM).body.applyForceToCenter(directionVec, true);
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
