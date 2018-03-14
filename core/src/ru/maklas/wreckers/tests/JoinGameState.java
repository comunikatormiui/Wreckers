package ru.maklas.wreckers.tests;

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
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.BodySyncEvent;
import ru.maklas.wreckers.network.events.EntityCreationEvent;
import ru.maklas.wreckers.network.events.WreckerSyncEvent;

public class JoinGameState extends State implements SocketProcessor {

    private final Socket socket;
    GameModel model;
    Engine engine;
    OrthographicCamera cam;
    private PhysicsDebugSystem debugSystem;

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

        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new TTLSystem());
        engine.add(new AntiGravSystem());
        engine.add(new MotorSystem());
        engine.add(new StatusEffectSystem());
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);

        engine.add(new JoinDamageSystem());
        engine.add(new JoinPickUpSystem(model));

        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);
        model.setSocket(socket);

        ContactListener worldListener = new JoinContactListener(engine);
        world.setContactListener(worldListener);

    }

    @Override
    protected void update(float dt) {
        socket.receive(this);
        engine.update(dt);
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

    }

    @Override
    public void process(Object o, Socket socket, SocketIterator iterator) {
        Log.CLIENT.event(o);
        engine.dispatch(o);

        if (o instanceof BodySyncEvent){
            BodySyncEvent e = (BodySyncEvent) o;
            Entity entity = engine.getById(e.getId());
            if (entity != null){
                e.hardApply(entity.get(Mappers.physicsM).body);
            }

        } else if (o instanceof WreckerSyncEvent){
            WreckerSyncEvent e = (WreckerSyncEvent) o;
            Entity entity = engine.getById(e.getId());
            if (entity != null){
                entity.get(Mappers.motorM).direction.set(e.getMotorX(), e.getMotorY());
                e.getPos().hardApply(entity.get(Mappers.physicsM).body);
            }

        } else if (o instanceof EntityCreationEvent){
            EntityCreationEvent e = (EntityCreationEvent) o;
            Entity entity = null;
            switch (e.getEntity()){
                case WRECKER:
                    entity = new EntityPlayer(e.getId(),  e.getX(), e.getY(), 10000, model, EntityType.PLAYER);
                    model.setPlayer(entity);
                    break;
                case FLOOR:
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
                    entity = new GameEntity(e.getId(), EntityType.OBSTACLE,  e.getX(), e.getY(), 0).add(new PhysicsComponent(platformBody));
                    break;
                case WEAPON_SWORD:
                    entity = new EntitySword(e.getId(), e.getX(), e.getY(), 10, model);
                    break;
            }

            if (entity != null){
                engine.add(entity);
            }
        }
    }
}
