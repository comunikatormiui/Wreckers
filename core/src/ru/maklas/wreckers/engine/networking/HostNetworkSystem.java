package ru.maklas.wreckers.engine.networking;

import com.badlogic.gdx.utils.ImmutableArray;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.NetBatch;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.wrecker.WSocket;

public class HostNetworkSystem extends NetworkSystem {

	ImmutableArray<Entity> weapons;

	public HostNetworkSystem() {
		super();
	}

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		weapons = engine.entitiesFor(WeaponComponent.class);
	}


	/**
	 * Синхронизуем игрока и все оружия кроме того что сейчас прикреплён к оппоненту
	 */
	@Override
	protected void sync(){
		sendSynchWrecker(engine.getBundler().get(B.player));
		final Entity opponent = engine.getBundler().get(B.opponent);

		if (opponent == null) {
			syncAllWeapons();
			return;
		}

		final WSocketComponent sc = opponent.get(M.wSocket);
		if (sc.allEmpty()){
			syncAllWeapons();
		} else {
			for (Entity weapon : weapons) {
				if (!contains(sc.sockets, weapon)) {
					sendSynchWeapon(weapon);
				}
			}
		}
	}

	private NetBatch batch = new NetBatch();
	private void syncAllWeapons(){

		for (Entity weapon : weapons) {
			sendSynchWeapon(batch, weapon);
		}
		System.out.println(batch.size());
		socket.send(batch);
		batch.clear();
	}


	private  boolean contains(WSocket[] arr, @NotNull Entity weapon){
		for (WSocket wSocket : arr) {
			if (wSocket.attachedEntity == weapon){
				return true;
			}
		}
		return false;
	}
}
