package ru.maklas.wreckers.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import ru.maklas.mengine.*;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.EntityUtils;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.DamageSystem;
import ru.maklas.wreckers.engine.movemnet.AntiGravSystem;
import ru.maklas.wreckers.engine.movemnet.MotorSystem;
import ru.maklas.wreckers.engine.other.EntityDebugSystem;
import ru.maklas.wreckers.engine.other.FrameTrackSystem;
import ru.maklas.wreckers.engine.other.TTLSystem;
import ru.maklas.wreckers.engine.physics.HostCollisionSystem;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.physics.PhysicsSystem;
import ru.maklas.wreckers.engine.rendering.CameraSystem;
import ru.maklas.wreckers.engine.rendering.RenderingSystem;
import ru.maklas.wreckers.engine.status_effects.StatusEffectSystem;
import ru.maklas.wreckers.engine.weapon.AttachRequest;
import ru.maklas.wreckers.engine.weapon.PickUpSystem;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.engine.weapon.GrabZoneChangeRequest;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.user_interface.GameUI;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.TimeSlower;
import ru.maklas.wreckers.utils.Utils;

public class SinglePlayerState extends AbstractEngineState implements GameController {

	private OrthographicCamera cam;
	private GameUI ui;
	private TimeSlower ts;

	@Override
	protected void loadAssets() {
		A.images.load();
		A.physics.load();

		cam = new OrthographicCamera(1280, 720);
		engine = new Engine();
		ui = new GameUI(this);
		ts = new TimeSlower();

	}

	@Override
	protected void fillBundler(Bundler bundler) {
		bundler.set(B.timeSlower, ts);
		bundler.set(B.world, A.physics.world);
		bundler.set(B.batch, batch);
		bundler.set(B.builders, A.physics.builders);
		bundler.set(B.gsmState, this);
		bundler.set(B.cam, cam);
		bundler.set(B.dt, 1 / 60f);
		B.fillIds(bundler);
	}

	@Override
	protected void addSystems(Engine engine) {
		engine.add(new FrameTrackSystem());
		engine.add(new PhysicsSystem());
		engine.add(new RenderingSystem());
		engine.add(new EntityDebugSystem());
		engine.add(new TTLSystem());
		engine.add(new AntiGravSystem());
		engine.add(new MotorSystem());
		engine.add(new StatusEffectSystem());
		engine.add(new CameraSystem());
		engine.add(new UpdatableEntitySystem());
		engine.add(new HostCollisionSystem());
		engine.add(new DamageSystem());
		engine.add(new PickUpSystem());
	}

	@Override
	protected void addDefaultEntities(Engine engine) {
		Body platformBody = A.physics.builders
				.newBody(BodyDef.BodyType.StaticBody)
				.addFixture(
						A.physics.builders.newFixture()
								.shape(A.physics.builders.buildRectangle(0, 0, 2500, 100))
								.friction(0.1f)
								.density(10)
								.bounciness(0.2f)
								.mask(EntityType.OBSTACLE)
								.build(), new FixtureData(FixtureType.OBSTACLE))
				.pos(-1000, 200)
				.type(BodyDef.BodyType.StaticBody)
				.linearDamp(0)
				.build();

		final Entity player = new EntityWrecker(1, EntityType.PLAYER,   0, 500, 10000);
		final EntityWrecker opponent = new EntityWrecker(2, EntityType.OPPONENT, 200, 700, 10000);
		final EntitySword sword = new EntitySword(3, 300, 700);
		final EntitySword sword2 = new EntitySword(4, -300, 300);
		final EntityHammer hammer = new EntityHammer(5, 0, 300);
		final Entity scythe = new EntityScythe(6, 370, 300);
		final Entity platform = new GameEntity(-2, EntityType.OBSTACLE, 0, 0, 0).add(new PhysicsComponent(platformBody));

		engine.getBundler().set(B.player, player);
		engine.getBundler().set(B.opponent, opponent);

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
		engine.dispatch(new AttachRequest(engine.findById(2), engine.findById(3)));
	}

	private Entity getPlayer(){
		return engine.getBundler().get(B.player);
	}

	@Override
	public void onDropClicked() {
		engine.dispatch(new DetachRequest(DetachRequest.Type.FIRST, engine.getBundler().get(B.player), null));
	}

	@Override
	public void onAttachDown() {
		engine.dispatch(new GrabZoneChangeRequest(true, getPlayer()));
	}

	@Override
	public void onAttachUp() {
		engine.dispatch(new GrabZoneChangeRequest(false, getPlayer()));
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
					engine.dispatch(new GrabZoneChangeRequest(true, getPlayer()));
				} else if (Input.Keys.O == keycode) {
					engine.dispatch(new DetachRequest(DetachRequest.Type.FIRST, getPlayer(), null));
				}
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (Input.Keys.P == keycode) {
					engine.dispatch(new GrabZoneChangeRequest(false, getPlayer()));
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
		dt = ts.convert(dt);
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

		getPlayer().get(M.motor).direction.set(directionVec);
		ui.act(dt);

		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			setState(new SinglePlayerState());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.E) && engine instanceof TestEngine) {
			Log.debug(((TestEngine) engine).captureResults());
		}
	}

	@Override
	protected void render(Batch batch) {
		cam.update(true);
		batch.setProjectionMatrix(cam.combined);
		batch.setColor(Color.WHITE);
		batch.begin();
		engine.render();
		batch.end();
		engine.getSystemManager().getSystem(PhysicsSystem.class).renderDebug();

		if (true || Gdx.app.getType() == Application.ApplicationType.Android) {
			ui.draw();
		}
	}

	@Override
	protected void dispose() {
		engine.dispose();
		A.physics.dispose();
	}
}
