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
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.GSMSet;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.state_change.NetRestartEvent;
import ru.maklas.wreckers.network.events.sync.BodySyncEvent;
import ru.maklas.wreckers.network.events.sync.WreckerSyncEvent;

public class JoinGameState extends State implements SocketProcessor {

    private final Socket socket;
    GameModel model;
    Engine engine;
    OrthographicCamera cam;
    private PhysicsDebugSystem debugSystem;
    private InputProcessor input;

    public JoinGameState(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected void onCreate() {
        Images.load();
        cam = new OrthographicCamera(1280, 720);
        engine = new Engine();
        World world = new World(new Vector2(0, -9.8f), true);

        model = new GameModel();
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);
        model.setSocket(socket);
        model.setCamera(cam);
        model.setGsm(getGsm());
        model.setCurrentState(this);

        engine.add(new RenderingSystem(batch, cam));
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);
        engine.add(new JoinDamageSystem());
        engine.add(new JoinPickUpSystem(model));

        engine.add(new MotorSystem());
        engine.add(new AntiGravSystem());
        engine.add(new StatusEffectSystem());
        engine.add(new PhysicsSystem(world));
        engine.add(new TTLSystem());
        engine.add(new JoinNetworkSystem(model));


        ContactListener worldListener = new JoinContactListener(engine);
        world.setContactListener(worldListener);

        input = new KeyboardGameInput(new JoinInputController(model));
    }

    @Override
    protected InputProcessor getInput() {
        return input;
    }

    @Override
    protected void update(float dt) {
        socket.receive(this);
        handleKeyboardInput();
        engine.update(dt);
        updateCamera();
    }


    private void handleKeyboardInput(){
        Entity player = model.getPlayer();
        if (player == null){
            return;
        }
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

        player.get(Mappers.motorM).direction.set(directionVec);
    }

    private void updateCamera(){
        if (model.getPlayer() != null) {
            cam.position.set(model.getPlayer().x, model.getPlayer().y, 0);
        }
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        engine.render();
        batch.end();
        debugSystem.update(9);
    }

    @Override
    protected void dispose() {
        model.getEngine().removeAllEntities();
    }

    @Override
    public void process(Object o, Socket socket, SocketIterator iterator) {
        if (!((o instanceof BodySyncEvent) || (o instanceof WreckerSyncEvent)))Log.CLIENT.event(o);


        if (o instanceof NetRestartEvent){
            model.getGsm().setCommand(new GSMSet(model.getCurrentState(), new JoinGameState(model.getSocket())));
            iterator.stop();
        }

        engine.dispatch(o);
    }
}
