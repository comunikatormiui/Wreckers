package ru.maklas.wreckers.engine.networking;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.wrecker.WSocket;

public class WSocketComponent implements Component{

	public final WSocket[] sockets;
	public final int attachedEntityType;

	public WSocketComponent(WSocket[] sockets, int attachedEntityType) {
		this.sockets = sockets;
		this.attachedEntityType = attachedEntityType;
	}

	public WSocketComponent(int sockets, int attachedEntityType) {
		this.sockets = new WSocket[sockets];
		this.attachedEntityType = attachedEntityType;
		for (int i = 0; i < sockets; i++) {
			this.sockets[i] = new WSocket();
		}

	}

	public int size() {
		return sockets.length;
	}

	@Nullable
	public WSocket firstEmpty(){
		for (WSocket socket : sockets) {
			if (socket.isEmpty()){
				return socket;
			}
		}
		return null;
	}

	@Nullable
	public WSocket find(Entity entity) {
		for (WSocket socket : sockets) {
			if (entity == socket.attachedEntity){
				return socket;
			}
		}
		return null;
	}

	@Nullable
	public WSocket firstAttached() {
		for (WSocket socket : sockets) {
			if (socket.attachedEntity != null){
				return socket;
			}
		}
		return null;
	}

	public boolean allEmpty() {
		return firstAttached() == null;
	}
}
