package ru.maklas.wreckers.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.BundlerKey;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.statics.ID;
import ru.maklas.wreckers.user_interface.GameUI;
import ru.maklas.wreckers.utils.TimeSlower;
import ru.maklas.wreckers.utils.gsm_lib.State;
import ru.maklas.wreckers.utils.net_dispatcher.NetDispatcher;
import ru.maklas.wreckers.utils.physics.Builders;

/** Для Engine.bundler **/
public class B {

	public static final BundlerKey<Batch> batch = BundlerKey.of("batch");
	public static final BundlerKey<OrthographicCamera> cam = BundlerKey.of("cam");
	public static final BundlerKey<World> world = BundlerKey.of("world");
	public static final BundlerKey<Builders> builders = BundlerKey.of("builders");
	public static final BundlerKey<Float> dt = BundlerKey.of("dt");
	public static final BundlerKey<Socket> socket = BundlerKey.of("socket");
	public static final BundlerKey<State> gsmState = BundlerKey.of("state");
	public static final BundlerKey<NetDispatcher> netD = BundlerKey.of("net");
	public static final BundlerKey<Entity> player = BundlerKey.of("player");
	public static final BundlerKey<Entity> opponent = BundlerKey.of("opponent");
	public static final BundlerKey<Boolean> isClient = BundlerKey.of("isClient"); //Is this client or server engine.
	public static final BundlerKey<Boolean> updateThisFrame = BundlerKey.of("urgentUpdate");
	public static final BundlerKey<TimeSlower> timeSlower = BundlerKey.of("timeSlower");


	public static final BundlerKey<GameUI> ui = BundlerKey.of("ui");
	public static final BundlerKey<Counter> idWeapons = BundlerKey.of("idWeapons");


	public static void fillIds(Bundler bundler){
		bundler.set(idWeapons, ID.counterForEnvironment());
	}
}
