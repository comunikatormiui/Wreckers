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
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.client.entities.ClientEntityPlayer;
import ru.maklas.wreckers.client.entities.ClientEntityZombie;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PlayerComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.requests.ShootRequest;
import ru.maklas.wreckers.engine.events.requests.WeaponChangeRequest;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
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

        final Entity player = new ClientEntityPlayer(1, 0, 300, 100, model);
        model.setPlayer(player);

        Weapon pistol = WeaponAssets.createNew(WeaponType.PISTOL, 5, 50);
        PlayerComponent playerC = player.get(Mappers.playerM);
        playerC.bag.weapons.add(pistol);
        playerC.currentWeapon = pistol;

        Entity platform = new Entity();
        platform.add(new PhysicsComponent(platformBody));
        engine.add(player);
        engine.add(platform);
        engine.add(new ClientEntityZombie(2, 200, 500, 100, model));
        engine.subscribe(DeathEvent.class, new Listener<DeathEvent>() {

            int idCounter = 3;

            @Override
            public void receive(Signal<DeathEvent> signal, DeathEvent deathEvent) {
                if (deathEvent.getTarget().type == EntityType.ZOMBIE.type){
                    engine.add(new ClientEntityZombie(idCounter++, Utils.rand.nextFloat() * 1000 - 500, Utils.rand.nextFloat() * 1000 - 500, 100, model));
                    Weapon currentWeapon = player.get(Mappers.playerM).currentWeapon;
                    currentWeapon.incAmmo(15);
                    gsm.print("Ammo: " + currentWeapon.getAmmo());
                }
            }
        });
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
        Entity player = model.getPlayer();
        Body body = player.get(Mappers.physicsM).body;
        Vector2 orientation = Utils.vec1.set(body.getTransform().getOrientation()).scl(25);

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
            float velocity = player.get(Mappers.velocityM).velocity;
            directionVec.scl(velocity);
            body.applyForceToCenter(directionVec, true);
        }


        cam.position.set(player.x + orientation.x, player.y + orientation.y, cam.position.z);
        engine.update(dt);
        Vector2 realMouse = Utils.toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
        GameAssets.rotateBody(body, realMouse.x, realMouse.y);

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
