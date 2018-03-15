package ru.maklas.wreckers.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
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
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.game.entities.EntityScythe;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.creation.*;
import ru.maklas.wreckers.network.events.sync.BodySyncEvent;
import ru.maklas.wreckers.network.events.sync.WreckerSyncEvent;

public class HostGameState extends State implements SocketProcessor {

    private final Socket socket;
    GameModel model;
    OrthographicCamera cam;
    private PhysicsDebugSystem debugSystem;
    private InputProcessor input;

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
        model.setHost(true);
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);
        model.setSocket(socket);
        model.setCamera(cam);

        engine.add(new RenderingSystem(batch, cam));
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);
        engine.add(new HostCollisionSystem(model));
        engine.add(new HostDamageSystem(model));
        engine.add(new HostPickUpSystem(model));
        engine.add(new HostNetworkSystem(model));

        engine.add(new MotorSystem());
        engine.add(new AntiGravSystem());
        engine.add(new StatusEffectSystem());
        engine.add(new PhysicsSystem(world));
        engine.add(new TTLSystem());



        ContactListener worldListener = new HostContactListener(engine, model);
        world.setContactListener(worldListener);



        { //Set up floor
            int x = 0;
            int y = 0;
            int width = 2000;
            int height = 100;
            Entity floor = new ru.maklas.wreckers.game.entities.EntityPlatform(0, x, y, GameAssets.floorZ, width, height, model);
            PlatformCreationEvent netEvent = new PlatformCreationEvent(3, x, y, width, height);
            socket.send(netEvent);
            engine.add(floor);
        }

        { //setUp player
            Entity player = new ru.maklas.wreckers.game.entities.EntityWrecker(1, 0,   500, 10000, model, EntityType.PLAYER);
            model.setPlayer(player);
            WreckerCreationEvent netEvent = WreckerCreationEvent.fromEntity(player, false);
            socket.send(netEvent);
            engine.add(player);
        }

        { //Set up opponent
            Entity opponent = new ru.maklas.wreckers.game.entities.EntityWrecker(2, 250,   500, 10000, model, EntityType.OPPONENT);
            model.setOpponent(opponent);
            WreckerCreationEvent netEvent = WreckerCreationEvent.fromEntity(opponent, true);
            socket.send(netEvent);
            engine.add(opponent);
        }

        { //Set up sword
            Entity sword = new ru.maklas.wreckers.game.entities.EntitySword(10, -200,   600, GameAssets.swordZ, model);
            WeaponCreationEvent netEvent = new SwordCreationEvent(sword.id, sword.x, sword.y, 0);
            socket.send(netEvent);
            engine.add(sword);
        }

        { //Set up Hammer
            Entity hammer = new ru.maklas.wreckers.game.entities.EntityHammer(11, 200,   600, GameAssets.hammerZ, model);
            WeaponCreationEvent netEvent = new HammerCreationEvent(hammer.id, hammer.x, hammer.y, 0);
            socket.send(netEvent);
            engine.add(hammer);
        }

        { //Set up scythe
            Entity scythe = new EntityScythe(12, 400,   600, GameAssets.scytheZ, model);
            WeaponCreationEvent netEvent = new ScytheCreationEvent(scythe.id, scythe.x, scythe.y, 0);
            socket.send(netEvent);
            engine.add(scythe);
        }

        input = new KeyboardGameInput(new HostInputController(model));
    }

    @Override
    protected InputProcessor getInput() {
        return input;
    }

    @Override
    protected void update(float dt) {
        socket.receive(this);

        handleKeyboardInput();
        model.getEngine().update(dt);
        updateCamera();
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

    @Override
    public void process(Object o, Socket socket, SocketIterator iterator) {
        if (!((o instanceof BodySyncEvent) || (o instanceof WreckerSyncEvent)))Log.CLIENT.event(o);
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
