package ru.maklas.wreckers.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet.Socket;
import ru.maklas.mnet.SocketProcessor;
import ru.maklas.mrudp.SocketIterator;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.client.entities.EntityPlayer;
import ru.maklas.wreckers.client.entities.EntitySword;
import ru.maklas.wreckers.client.entities.GameEntity;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.BodySyncEvent;
import ru.maklas.wreckers.network.events.EntityCreationEvent;
import ru.maklas.wreckers.network.events.WreckerSyncEvent;

public class HostGameState extends State implements SocketProcessor {

    private final Socket socket;
    GameModel model;
    OrthographicCamera cam;
    private PhysicsDebugSystem debugSystem;

    Entity platform;
    Entity player;
    EntitySword sword;

    public HostGameState(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected void onCreate() {
        Images.load();
        cam = new OrthographicCamera(1280, 720);
        Engine engine = new Engine();
        World world = new World(new Vector2(0, -9.8f), true);
        model = new GameModel();

        engine.add(new PhysicsSystem(world)); // both
        engine.add(new RenderingSystem(batch, cam)); // both
        engine.add(new TTLSystem()); //both
        engine.add(new AntiGravSystem()); //both
        engine.add(new MotorSystem());//both
        engine.add(new StatusEffectSystem());//both. Эффекты же будут разные
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale); //both

        engine.add(new HostCollisionSystem(model)); //server. У клиента будет похожая, но без активных действий типа detach
        engine.add(new HostDamageSystem(model)); //server. Расчёт урона только на сервере.
        engine.add(new HostPickUpSystem(model));//server. У клиента будет меньше проверок. Только действия


        model.setHost(true);
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);
        model.setSocket(socket);

        ContactListener worldListener = new HostContactListener(engine, model);
        world.setContactListener(worldListener);



        Body platformBody = model.getBuilder()
                .newBody()
                .addFixture(model.getFixturer().newFixture()
                        .shape(model.getShaper().buildRectangle(0, 0, 2000, 100))
                        .friction(0.1f)
                        .density(10)
                        .bounciness(0.2f)
                        .mask(EntityType.OBSTACLE)
                        .build(), new FixtureData(FixtureType.OBSTACLE))
                .pos(-360, 200)
                .type(BodyDef.BodyType.StaticBody)
                .linearDamp(0)
                .build();

        platform = new GameEntity(-2, EntityType.OBSTACLE, 0, 0, 0).add(new PhysicsComponent(platformBody));
        player = new EntityPlayer(1, 0,   500, 10000, model, EntityType.PLAYER);
        sword = new EntitySword(3, -200, 700, 10, model);
        model.setPlayer(player);

        engine.add(platform).add(player).add(sword);
        socket.send(EntityCreationEvent.fromEntity(EntityEnum.FLOOR, platform));
        socket.send(EntityCreationEvent.fromEntity(EntityEnum.WRECKER, player));
        socket.send(EntityCreationEvent.fromEntity(EntityEnum.WEAPON_SWORD, sword));

    }

    @Override
    protected InputProcessor getInput() {

        final Engine engine = model.getEngine();
        InputAdapter input = new InputAdapter() {
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
                if (Input.Keys.P == keycode) {
                    engine.dispatch(new GrabZoneChangeRequest(true, model.getPlayer()));
                } else if (Input.Keys.O == keycode) {
                    engine.dispatch(new DetachRequest(model.getPlayer(), DetachRequest.Type.FIRST, null));
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (Input.Keys.P == keycode) {
                    engine.dispatch(new GrabZoneChangeRequest(false, model.getPlayer()));
                }
                return true;
            }
        };

        return input;
    }

    @Override
    protected void update(float dt) {
        socket.receive(this);

        handleKeyboardInput();
        model.getEngine().update(dt);
        updateCamera();

        if (model.timeToUpdate()){
            synchronize();
            model.setSkipFrameForUpdate(5);
        } else {
            model.decSkipFrames();
        }
    }

    private void handleKeyboardInput(){
        Vector2 directionVec = Utils.vec2.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            directionVec.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            directionVec.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            directionVec.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            directionVec.add(1, 0);
        }

        model.getPlayer().get(Mappers.motorM).direction.set(directionVec);
    }

    private void updateCamera(){
        cam.position.set(model.getPlayer().x, model.getPlayer().y, 0);
    }


    private void synchronize(){
        Vector2 motor = player.get(Mappers.motorM).direction;
        socket.send(new WreckerSyncEvent(BodySyncEvent.fromBody(player.id, player.get(Mappers.physicsM).body), motor.x, motor.y));
        socket.send(BodySyncEvent.fromBody(sword.id, sword.get(Mappers.physicsM).body));
    }

    @Override
    public void process(Object o, Socket socket, SocketIterator iterator) {
        Log.SERVER.event(o);
        model.getEngine().dispatch(o);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        model.getEngine().render();
        batch.end();
        debugSystem.update(9);
    }

    @Override
    protected void dispose() {

    }
}
