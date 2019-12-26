package ru.maklas.wreckers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketProcessor;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.EntityUtils;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.JoinDamageSystem;
import ru.maklas.wreckers.engine.movemnet.AntiGravSystem;
import ru.maklas.wreckers.engine.movemnet.MotorSystem;
import ru.maklas.wreckers.engine.networking.JoinNetworkSystem;
import ru.maklas.wreckers.engine.other.TTLSystem;
import ru.maklas.wreckers.engine.physics.PhysicsSystem;
import ru.maklas.wreckers.engine.rendering.RenderingSystem;
import ru.maklas.wreckers.engine.status_effects.StatusEffectSystem;
import ru.maklas.wreckers.engine.weapon.JoinPickUpSystem;
import ru.maklas.wreckers.game.JoinInputController;
import ru.maklas.wreckers.game.KeyboardGameInput;
import ru.maklas.wreckers.net_events.state_change.NetRestartEvent;
import ru.maklas.wreckers.net_events.sync.NetBodySyncEvent;
import ru.maklas.wreckers.net_events.sync.NetWreckerSyncEvent;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.Utils;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;
import ru.maklas.wreckers.utils.physics.Builders;

public class JoinGameState extends AbstractEngineState implements SocketProcessor {

	private final Socket socket;
	OrthographicCamera cam;
	private InputProcessor input;

	public JoinGameState(Socket socket) {
		this.socket = socket;
	}

	@Override
	protected void loadAssets() {
		A.images.load();
		A.physics.load();
		cam = new OrthographicCamera(1280, 720);

	}

	@Override
	protected void fillBundler(Bundler bundler) {
		bundler.set(B.world, A.physics.world);
		bundler.set(B.netD, new NetDispatcher());
		bundler.set(B.batch, batch);
		bundler.set(B.builders, new Builders(A.physics.world, Game.scale));
		bundler.set(B.gsmState, this);
		bundler.set(B.cam, cam);
		bundler.set(B.dt, 1 / 60f);
		bundler.set(B.isClient, true);
		B.fillIds(bundler);
	}

	@Override
	protected void addSystems(Engine engine) {
		engine.add(new RenderingSystem());
		engine.add(new JoinDamageSystem());
		engine.add(new JoinPickUpSystem());

		engine.add(new MotorSystem());
		engine.add(new AntiGravSystem());
		engine.add(new StatusEffectSystem());
		engine.add(new PhysicsSystem());
		engine.add(new TTLSystem());
		engine.add(new JoinNetworkSystem());

	}


	@Override
	protected void addDefaultEntities(Engine engine) {
		input = new KeyboardGameInput(new JoinInputController(engine));
		engine.add(EntityUtils.camera(cam));
	}

	@Override
	protected void start() {

	}

	@Override
	protected InputProcessor getInput() {
		return input;
	}

	@Override
	protected void update(float dt) {
		socket.update(this);
		handleKeyboardInput();
		engine.update(dt);
	}


	private void handleKeyboardInput(){
		Entity player = engine.getBundler().get(B.player);
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

		player.get(M.motor).direction.set(directionVec);
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
	}

	@Override
	protected void dispose() {
		A.physics.dispose();
		super.dispose();
	}

	@Override
	public void process(Socket socket, Object o) {
		if (!((o instanceof NetBodySyncEvent) || (o instanceof NetWreckerSyncEvent))) Log.debug(o);


		if (o instanceof NetRestartEvent){
			setState(new JoinGameState(socket));
			socket.stop();
			return;
		}

		engine.dispatch(o);
	}
}
