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
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.client.entities.ClientEntityPlayer;
import ru.maklas.wreckers.client.entities.ClientEntityZombie;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PlayerInventoryComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.requests.ShootRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponChangeRequest;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.libs.SimpleProfiler;
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
        System.out.println(cam.position);
        engine = new Engine();
        world = new World(new Vector2(0, 0), true);
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

            }
        });
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);


        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new HealthSystem());
        engine.add(new ClientShooterSystem(world));
        engine.add(new TTLSystem());
        engine.add(new ZombieSystem());

        model = new ClientGameModel();
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);



        Body platformBody = model.getBuilder()
                .newBody()
                .addFixture(model.getFixturer().newFixture()
                        .shape(model.getShaper().buildRectangle(0, 0, 720, 100))
                        .friction(0)
                        .density(10)
                        .bounciness(1)
                        .mask(EntityType.OBSTACLE)
                        .build())
                .pos(-360, 200)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        Entity player = new ClientEntityPlayer(1, 0, 300, 100, model);
        model.setPlayer(player);

        Weapon pistol = WeaponAssets.createNew(WeaponType.PISTOL, 5, 50);
        PlayerInventoryComponent inventory = player.get(Mappers.inventoryM);
        inventory.bag.weapons.add(pistol);
        inventory.currentWeapon = pistol;

        Entity platform = new Entity();
        platform.add(new PhysicsComponent(platformBody));
        engine.add(player);
        engine.add(platform);
        engine.add(new ClientEntityZombie(2, 200, 500, 100, model));
    }

    @Override
    protected InputProcessor getInput() {
        return new InputAdapter(){
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                GameAssets.rotateBody(model.getPlayer().get(Mappers.physicsM).body, realMouse.x, realMouse.y);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector2 realMouse = Utils.toScreen(screenX, screenY, cam);
                GameAssets.rotateBody(model.getPlayer().get(Mappers.physicsM).body, realMouse.x, realMouse.y);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.RIGHT){
                    return false;
                }

                engine.dispatch(new ShootRequest(model.getPlayer()));

                if (true){
                    return true;
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.NUM_1){

                }

                switch (keycode){

                    case Input.Keys.NUM_1:
                        engine.dispatch(new WeaponChangeRequest(model.getPlayer(), WeaponType.NONE));
                        break;
                    case Input.Keys.NUM_2:
                        engine.dispatch(new WeaponChangeRequest(model.getPlayer(), WeaponType.PISTOL));
                        break;
                }
                return true;
            }
        };
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        Vector2 realMouse = Utils.toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
        Body body = model.getPlayer().get(Mappers.physicsM).body;
        GameAssets.rotateBody(body, realMouse.x, realMouse.y);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            float velocity = model.getPlayer().get(Mappers.velocityM).velocity;
            Utils.vec1.set(body.getTransform().getOrientation()).scl(velocity);
            body.applyForceToCenter(Utils.vec1, true);
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
