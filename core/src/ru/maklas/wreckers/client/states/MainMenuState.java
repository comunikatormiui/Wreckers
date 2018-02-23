package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.ClientGameModel;
import ru.maklas.wreckers.client.entities.*;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.GrabZoneComponent;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.events.CollisionEvent;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.engine.events.requests.AttachRequest;
import ru.maklas.wreckers.engine.systems.*;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.libs.gsm_lib.State;
import sun.misc.Unsafe;

public class MainMenuState extends State {

    Engine engine;
    World world;
    PhysicsDebugSystem debugSystem;
    OrthographicCamera cam;
    ClientGameModel model;
    Stage stage;
    Touchpad touchpad;

    @Override
    protected void onCreate() {
        Images.load();

        cam = new OrthographicCamera(1280, 720);
        engine = new Engine();
        world = new World(new Vector2(0, -9.8f), true);

        setUpStage();

        engine.add(new PhysicsSystem(world));
        engine.add(new RenderingSystem(batch, cam));
        engine.add(new CollisionSystem());
        engine.add(new DamageSystem());
        engine.add(new TTLSystem());
        engine.add(new AntiGravSystem());
        engine.add(new PickUpSystem(world));
        engine.add(new MotorSystem());


        model = new ClientGameModel();
        model.setBuilder(new BodyBuilder(world, GameAssets.box2dScale));
        model.setEngine(engine);
        model.setFixturer(new FDefBuilder());
        model.setShaper(new ShapeBuilder(GameAssets.box2dScale));
        model.setWorld(world);


        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // Sensor Overlap
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if (fixtureA.isSensor() && fixtureB.isSensor()){
                    handleBothSensors(contact, fixtureA, fixtureB);
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                {   //COLLISION EVENT
                    Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
                    Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
                    CollisionEvent event = new CollisionEvent(a, b, contact, impulse, true);
                    engine.dispatch(event);
                }
            }

            private void handleBothSensors(Contact contact, final Fixture fixtureA, final Fixture fixtureB) {
                FixtureData udA;
                FixtureData udB;
                Entity eA = (Entity) fixtureA.getBody().getUserData();
                Entity eB = (Entity) fixtureB.getBody().getUserData();
                try {
                    udA = (FixtureData) fixtureA.getUserData();
                    udB = (FixtureData) fixtureB.getUserData();
                } catch (Exception e) {
                    System.err.println(eA + " OR " + eB + " have no fixture data on some of their fixtures");
                    e.printStackTrace();
                    return;
                }
                FixtureType type1 = udA.getFixtureType();
                FixtureType type2 = udB.getFixtureType();

                if (type1 == FixtureType.PICKUP_SENSOR && type2 == FixtureType.GRABBER_SENSOR){
                    final PickUpComponent wPick = eA.get(Mappers.pickUpM);
                    final GrabZoneComponent pPick = eB.get(Mappers.grabM);
                    if (wPick.pickUpZoneEnabled() && pPick.enabled()){
                        engine.dispatchLater(new AttachRequest(eB, pPick, eA, wPick));
                    }
                } else if (type1 == FixtureType.GRABBER_SENSOR && type2 == FixtureType.PICKUP_SENSOR){
                    final PickUpComponent wPick = eB.get(Mappers.pickUpM);
                    final GrabZoneComponent pPick = eA.get(Mappers.grabM);
                    if (wPick.pickUpZoneEnabled() && pPick.enabled()){
                        engine.dispatchLater(new AttachRequest(eA, pPick, eB, wPick));
                    }
                }
            }
        });
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

        final Entity player         = new EntityPlayer(1, 0,   500, 10000, model, EntityType.PLAYER);
        final EntityPlayer opponent = new EntityPlayer(2, 200, 500, 10000, model, EntityType.OPPONENT);
        final EntitySword sword = new EntitySword(3, -200, 700, 10, model);
        final EntitySword sword2 = new EntitySword(4, 0, 300, 10, model);
        final EntityHammer hammer = new EntityHammer(5, -200, 300, 10, model);
        final Entity platform = new GameEntity(-2, EntityType.OBSTACLE, 0, 0, 0).add(new PhysicsComponent(platformBody));

        model.setPlayer(player);
        model.setOpponent(opponent);

        engine.add(player);
        engine.add(opponent);
        engine.add(sword);
        engine.add(sword2);
        engine.add(hammer);
        engine.add(platform);


        engine.dispatch(new GrabZoneChangeRequest(true, opponent));
    }

    private static boolean enableStage = false;
    private void setUpStage() {
        stage = new Stage();

        if (enableStage) {
            touchpad = new Touchpad(10, Images.touchStyle);
            touchpad.setBounds(15, 15, 200, 200);
            TextureRegionDrawable block = new TextureRegionDrawable(Images.touchBlock);
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(block, block, block, Images.font);
            TextButton pickUpButton = new TextButton("Pick up", style);
            TextButton dropButton = new TextButton("Drop", style);
            pickUpButton.setColor(Color.GREEN);
            dropButton.setColor(Color.RED);


            pickUpButton.setPosition(200, 0);
            pickUpButton.sizeBy(2);
            dropButton.setPosition(72 + 200, 0);
            stage.addActor(touchpad);
            stage.addActor(pickUpButton);
            stage.addActor(dropButton);


            dropButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    engine.dispatch(new DetachRequest(model.getPlayer(), DetachRequest.Type.FIRST, null));
                }
            });


            pickUpButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    engine.dispatch(new GrabZoneChangeRequest(true, model.getPlayer()));
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    engine.dispatch(new GrabZoneChangeRequest(false, model.getPlayer()));
                }
            });
        }

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


        return new InputMultiplexer(stage, input);
    }

    @Override
    protected void update(float dt) {
        model.getEngine().update(dt);
        cam.position.set(model.getPlayer().x, model.getPlayer().y, 0);

        Vector2 directionVec = Utils.vec2.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            directionVec.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            directionVec.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            directionVec.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            directionVec.add(1, 0);
        }

        model.getPlayer().get(Mappers.motorM).direction.set(directionVec);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        engine.render();
        batch.end();
        debugSystem.update(0);
        stage.draw();
    }

    @Override
    protected void dispose() {

    }
}
