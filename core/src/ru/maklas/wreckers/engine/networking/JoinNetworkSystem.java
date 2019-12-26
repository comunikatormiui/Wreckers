package ru.maklas.wreckers.engine.networking;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.wrecker.WSocket;
import ru.maklas.wreckers.engine.wrecker.WSocketComponent;

public class JoinNetworkSystem extends NetworkSystem {

	public JoinNetworkSystem() {
		super();
	}

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
	}

	/** Синхронизуем своё тело и свои оружия если имеются **/
	@Override
	protected void sync(){
		final Entity player = engine.getBundler().get(B.player);
		if (player == null) return;

		sendSynchWrecker(player);
		final WSocketComponent sc = player.get(M.wSocket);
		for (WSocket socket : sc.sockets) {
			if (socket.attachedEntity != null){
				sendSynchWeapon(socket.attachedEntity);
			}
		}
	}
}
