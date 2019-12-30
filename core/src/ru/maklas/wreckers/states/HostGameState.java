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
import ru.maklas.wreckers.engine.health.HostDamageSystem;
import ru.maklas.wreckers.engine.movemnet.AntiGravSystem;
import ru.maklas.wreckers.engine.movemnet.MotorSystem;
import ru.maklas.wreckers.engine.networking.HostNetworkSystem;
import ru.maklas.wreckers.engine.other.FrameTrackSystem;
import ru.maklas.wreckers.engine.other.TTLSystem;
import ru.maklas.wreckers.engine.physics.HostCollisionSystem;
import ru.maklas.wreckers.engine.physics.PhysicsSystem;
import ru.maklas.wreckers.engine.rendering.CameraSystem;
import ru.maklas.wreckers.engine.rendering.RenderingSystem;
import ru.maklas.wreckers.engine.status_effects.StatusEffectSystem;
import ru.maklas.wreckers.engine.weapon.HostPickUpSystem;
import ru.maklas.wreckers.game.HostInputController;
import ru.maklas.wreckers.game.KeyboardGameInput;
import ru.maklas.wreckers.game.entities.*;
import ru.maklas.wreckers.net_events.creation.*;
import ru.maklas.wreckers.net_events.sync.NetBodySyncEvent;
import ru.maklas.wreckers.net_events.sync.NetWreckerSyncEvent;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.ID;
import ru.maklas.wreckers.statics.Layers;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.Utils;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;

public class HostGameState extends AbstractEngineState implements SocketProcessor {

	private final Socket socket;
	private OrthographicCamera cam;
	private InputProcessor input;
	private NetDispatcher netD;

	public HostGameState(Socket socket) {
		this.socket = socket;
	}


	@Override
	protected void loadAssets() {
		A.images.load();
		A.physics.load();
		cam = new OrthographicCamera(1280, 720);
		netD = new NetDispatcher();
	}

	@Override
	protected void fillBundler(Bundler bundler) {
		bundler.set(B.world, A.physics.world);
		bundler.set(B.batch, batch);
		bundler.set(B.builders, A.physics.builders);
		bundler.set(B.netD, netD);
		bundler.set(B.gsmState, this);
		bundler.set(B.socket, socket);
		bundler.set(B.cam, cam);
		bundler.set(B.dt, 1 / 60f);
		bundler.set(B.isClient, false);
		B.fillIds(bundler);
	}

	@Override
	protected void addSystems(Engine engine) {
		engine.add(new FrameTrackSystem());
		engine.add(new RenderingSystem());
		engine.add(new HostCollisionSystem());
		engine.add(new HostDamageSystem());
		engine.add(new HostPickUpSystem());
		engine.add(new CameraSystem());

		engine.add(new MotorSystem());
		engine.add(new AntiGravSystem());
		engine.add(new StatusEffectSystem());
		engine.add(new PhysicsSystem());
		engine.add(new TTLSystem());
		engine.add(new HostNetworkSystem());

	}

	@Override
	protected void addDefaultEntities(Engine engine) {
		{ //Set up floor
			int x = 0;
			int y = 0;
			int width = 2000;
			int height = 100;
			Entity floor = new EntityPlatform(ID.floor, x, y, Layers.floorZ, width, height);
			NetPlatformCreationEvent netEvent = new NetPlatformCreationEvent(3, x, y, width, height);
			socket.send(netEvent);
			engine.add(floor);
		}

		{ //setUp player
			Entity player = new EntityWrecker(ID.multiplayerHost, EntityType.PLAYER,   0, 500, 10000);
			engine.getBundler().set(B.player, player);
			NetWreckerCreationEvent netEvent = NetWreckerCreationEvent.fromEntity(player, false);
			socket.send(netEvent);
			engine.add(player);
		}

		{ //Set up opponent
			Entity opponent = new EntityWrecker(ID.multiplayerJoin, EntityType.OPPONENT,   250, 500, 10000);
			engine.getBundler().set(B.opponent, opponent);
			NetWreckerCreationEvent netEvent = NetWreckerCreationEvent.fromEntity(opponent, true);
			socket.send(netEvent);
			engine.add(opponent);
		}

		{ //Set up sword
			Entity sword = new EntitySword(engine.getBundler().get(B.idWeapons).next(), -200,   600);
			NetWeaponCreationEvent netEvent = new NetSwordCreationEvent(sword.id, sword.x, sword.y, 0);
			socket.send(netEvent);
			engine.add(sword);
		}

		{ //Set up Hammer
			Entity hammer = new EntityHammer(engine.getBundler().get(B.idWeapons).next(), 200,   600);
			NetWeaponCreationEvent netEvent = new NetHammerCreationEvent(hammer.id, hammer.x, hammer.y, 0);
			socket.send(netEvent);
			engine.add(hammer);
		}

		{ //Set up scythe
			Entity scythe = new EntityScythe(engine.getBundler().get(B.idWeapons).next(), 400,   600, Layers.scytheZ);
			NetWeaponCreationEvent netEvent = new NetScytheCreationEvent(scythe.id, scythe.x, scythe.y, 0);
			socket.send(netEvent);
			engine.add(scythe);
		}

		{ //Camera
			engine.add(EntityUtils.camera(cam, engine.getBundler().get(B.player).id));
		}
	}

	@Override
	protected void start() {
		input = new KeyboardGameInput(new HostInputController(engine));
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

		engine.getBundler().get(B.player).get(M.motor).direction.set(directionVec);
	}

	@Override
	public void process(Socket socket, Object o) {
		if (!((o instanceof NetBodySyncEvent) || (o instanceof NetWreckerSyncEvent)))Log.debug(o);
		netD.process(socket, o);
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
		engine.removeAllEntities();
		A.physics.world.dispose();
	}
}
