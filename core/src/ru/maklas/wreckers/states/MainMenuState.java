package ru.maklas.wreckers.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.EntityUtils;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.HostDamageSystem;
import ru.maklas.wreckers.engine.movemnet.AntiGravSystem;
import ru.maklas.wreckers.engine.movemnet.MotorSystem;
import ru.maklas.wreckers.engine.other.TTLSystem;
import ru.maklas.wreckers.engine.physics.HostCollisionSystem;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.physics.PhysicsSystem;
import ru.maklas.wreckers.engine.rendering.CameraSystem;
import ru.maklas.wreckers.engine.rendering.RenderingSystem;
import ru.maklas.wreckers.engine.status_effects.StatusEffectSystem;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.engine.weapon.GrabZoneChangeRequest;
import ru.maklas.wreckers.engine.weapon.HostPickUpSystem;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.user_interface.GameUI;
import ru.maklas.wreckers.utils.Utils;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;
import ru.maklas.wreckers.utils.physics.Builders;

public class MainMenuState extends AbstractEngineState implements GameController {

	private OrthographicCamera cam;
	private GameModel model;
	private GameUI ui;

	@Override
	protected void loadAssets() {
		A.images.load();
		A.physics.load();

		cam = new OrthographicCamera(1280, 720);
		engine = new Engine();
		ui = new GameUI(this);

		model = new GameModel();
		model.setBuilder(new Builders(A.physics.world, Game.scale));
		model.setWorld(A.physics.world);
	}

	@Override
	protected void fillBundler(Bundler bundler) {
		model.setEngine(engine);
		bundler.set(B.world, A.physics.world);
		bundler.set(B.netD, new NetDispatcher());
		bundler.set(B.batch, batch);
		bundler.set(B.builders, A.physics.builders);
		bundler.set(B.gsmState, this);
		bundler.set(B.cam, cam);
		bundler.set(B.dt, 1 / 60f);
		B.fillIds(bundler);
	}

	@Override
	protected void addSystems(Engine engine) {
		engine.add(new PhysicsSystem());
		engine.add(new RenderingSystem());
		engine.add(new HostCollisionSystem());
		engine.add(new HostDamageSystem());
		engine.add(new TTLSystem());
		engine.add(new AntiGravSystem());
		engine.add(new HostPickUpSystem());
		engine.add(new MotorSystem());
		engine.add(new StatusEffectSystem());
		engine.add(new CameraSystem());
	}

	@Override
	protected void addDefaultEntities(Engine engine) {
		Body platformBody = A.physics.builders
				.newBody(BodyDef.BodyType.StaticBody)
				.addFixture(
						A.physics.builders.newFixture()
								.shape(A.physics.builders.buildRectangle(0, 0, 2000, 100))
								.friction(0.1f)
								.density(10)
								.bounciness(0.2f)
								.mask(EntityType.OBSTACLE)
								.build(), new FixtureData(FixtureType.OBSTACLE))
				.pos(-360, 200)
				.type(BodyDef.BodyType.StaticBody)
				.linearDamp(0)
				.build();

		final Entity player = new EntityWrecker(1, EntityType.PLAYER,   0, 500, 10000);
		final EntityWrecker opponent = new EntityWrecker(2, EntityType.OPPONENT, 200, 500, 10000);
		final EntitySword sword = new EntitySword(3, -200, 700, 10);
		final EntitySword sword2 = new EntitySword(4, 0, 300, 10);
		final EntityHammer hammer = new EntityHammer(5, -200, 300, 10);
		final Entity scythe = new EntityScythe(6, 370, 300, 10);
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
		engine.add(EntityUtils.camera(cam, player.id));
	}

	@Override
	protected void start() {

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
		engine.update(dt);

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

		model.getPlayer().get(M.motor).direction.set(directionVec);
		ui.act(dt);
	}

	@Override
	protected void render(Batch batch) {
		cam.update();
		batch.setColor(Color.WHITE);
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		engine.render();
		batch.end();
		engine.getSystemManager().getSystem(PhysicsSystem.class).renderDebug();

		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			ui.draw();
		}
	}

	@Override
	protected void dispose() {

	}
}
