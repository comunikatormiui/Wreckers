package ru.maklas.wreckers.game;

import com.badlogic.gdx.utils.Consumer;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.networking.WSocketComponent;
import ru.maklas.wreckers.engine.wrecker.WSocket;
import ru.maklas.wreckers.net_events.NetDetachRequest;
import ru.maklas.wreckers.net_events.NetGrabZoneChangeRequest;

public class JoinInputController implements InputController {

	private final Engine engine;
	private final Bundler bundler;
	private final Socket socket;

	public JoinInputController(Engine engine) {
		this.engine = engine;
		bundler = engine.getBundler();
		socket = bundler.get(B.socket);
	}

	@Override
	public void enableGrabZone() {
		forPlayer(p -> socket.send(new NetGrabZoneChangeRequest(p.id, true)));
	}

	@Override
	public void disableGrabZone() {
		forPlayer(p -> socket.send(new NetGrabZoneChangeRequest(p.id, false)));
	}

	@Override
	public void detachWeapon() {
		Entity player = getPlayer();
		if (player == null){
			return;
		}
		WSocketComponent sc = player.get(M.wSocket);
		if (sc == null){
			return;
		}
		WSocket wSocket = sc.firstAttached();
		if (wSocket == null){
			return;
		}
		socket.send(new NetDetachRequest(player.id, wSocket.attachedEntity.id));
	}

	@Override
	public void restart() {

	}

	private Entity getPlayer() {
		return bundler.get(B.player);
	}

	private void forPlayer(Consumer<Entity> c) {
		Entity player = getPlayer();
		if (player != null) {
			c.accept(player);
		}
	}

}
