package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;

public class MainMenuState extends State implements GameController {

    Engine engine;
    World world;
    PhysicsDebugSystem debugSystem;
    OrthographicCamera cam;
    GameModel model;
    GameUI ui;

    @Override
    protected void onCreate() {
        Images.load();

        cam = new OrthographicCamera(1280, 720);
        engine = new Engine();
        world = new World(new Vector2(0, -9.8f), true);
        ui = new GameUI(this);

        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new HostCollisionSystem(model));
        engine.add(new HostDamageSystem(model));
        engine.add(new TTLSystem());
        engine.add(new AntiGravSystem());
        engine.add(new HostPickUpSystem(model));
        engine.add(new MotorSystem());
        engine.add(new StatusEffectSystem());



        GameModel model = new GameModel();
        this.model = model;
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);


        world.setContactListener(new HostContactListener(engine, model));
        debugSystem = new PhysicsDebugSystem(world, cam, GameAssets.box2dScale);


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

        final Entity player         = new EntityWrecker(1, 0,   500, 10000, model, EntityType.PLAYER);
        final EntityWrecker opponent = new EntityWrecker(2, 200, 500, 10000, model, EntityType.OPPONENT);
        final EntitySword sword = new EntitySword(3, -200, 700, 10, model);
        final EntitySword sword2 = new EntitySword(4, 0, 300, 10, model);
        final EntityHammer hammer = new EntityHammer(5, -200, 300, 10, model);
        final Entity scythe = new EntityScythe(6, 370, 300, 10, model);
        final Entity platform = new GameEntity(-2, EntityType.OBSTACLE, 0, 0, 0).add(new PhysicsComponent(platformBody));

        model.setPlayer(player);
        model.setOpponent(opponent);

        engine.add(player);
        engine.add(opponent);
        engine.add(sword);
        engine.add(sword2);
        engine.add(hammer);
        engine.add(scythe);
        engine.add(platform);


        //engine.dispatch(new GrabZoneChangeRequest(true, opponent));
    }

    @Override
    public void onDropClicked() {
        engine.dispatch(new DetachRequest(DetachRequest.Type.FIRST, model.getPlayer(), null));
    }

    @Override
    public void onAttachDown() {
        engine.dispatch(new GrabZoneChangeRequest(true, model.getPlayer()));
    }

    @Override
    public void onAttachUp() {
        engine.dispatch(new GrabZoneChangeRequest(false, model.getPlayer()));
    }

    @Override
    protected InputProcessor getInput() {
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
                    engine.dispatch(new DetachRequest(DetachRequest.Type.FIRST, model.getPlayer(), null));
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


        if (Gdx.app.getType() == Application.ApplicationType.Android){
            return new InputMultiplexer(ui, input);
        } else {
            return input;
        }
    }

    @Override
    protected void update(float dt) {
        model.getEngine().update(dt);
        cam.position.set(model.getPlayer().x, model.getPlayer().y, 0);

        Vector2 directionVec = Utils.vec2.set(0, 0);
        if (Gdx.app.getType() == Application.ApplicationType.Android){

            directionVec.set(ui.getTouchX(), ui.getTouchY());

        } else if (Gdx.app.getType() == Application.ApplicationType.Desktop) {

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
        }

        model.getPlayer().get(Mappers.motorM).direction.set(directionVec);
        ui.act(dt);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        engine.render();
        batch.end();
        debugSystem.update(0);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.draw();
        }
    }

    @Override
    protected void dispose() {

    }
}
