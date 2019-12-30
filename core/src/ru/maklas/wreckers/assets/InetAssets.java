package ru.maklas.wreckers.assets;

import com.esotericsoftware.kryo.Kryo;
import ru.maklas.mnet2.*;
import ru.maklas.mnet2.serialization.KryoSerializer;
import ru.maklas.mnet2.serialization.Serializer;
import ru.maklas.wreckers.net_events.*;
import ru.maklas.wreckers.net_events.creation.*;
import ru.maklas.wreckers.net_events.state_change.NetRestartEvent;
import ru.maklas.wreckers.net_events.sync.NetBodySyncEvent;
import ru.maklas.wreckers.net_events.sync.NetWreckerSyncEvent;

public class InetAssets {


	public static final int defaultPort = 1228;
	public static final int defaultBufferSize = 512;
	public static final int defaultClientSocketUpdate = 75;
	public static final int defaultServerSocketUpdate = 100;

	public static Kryo newKryo(){
		Kryo kryo = new Kryo();

		//Connection
		kryo.register(NetConnectionRequest.class);
		kryo.register(NetConnectionResponse.class);

		//State
		kryo.register(NetRestartEvent.class);

		//Creation
		kryo.register(NetWreckerCreationEvent.class);
		kryo.register(NetWeaponCreationEvent.class);
		kryo.register(NetSwordCreationEvent.class);
		kryo.register(NetHammerCreationEvent.class);
		kryo.register(NetScytheCreationEvent.class);
		kryo.register(NetPlatformCreationEvent.class);

		//Synchronization
		kryo.register(NetBodySyncEvent.class);
		kryo.register(NetWreckerSyncEvent.class);

		//Game Events
		kryo.register(NetAttachDetachEvent.class);
		kryo.register(NetHitEvent.class);
		kryo.register(NetGrabZoneChange.class);
		kryo.register(NetGrabZoneChangeRequest.class);
		kryo.register(NetDetachRequest.class);


		return kryo;
	}

	private static Supplier<Serializer> provider = () -> new KryoSerializer(newKryo(), 512);
	public static Supplier<Serializer> serializerProvider(){
		return provider;
	}

	/**
	 * @param packetLossChance 0..100
	 */
	public static UDPSocket wrapSocket(UDPSocket sock, int ping, double packetLossChance) {
		if (packetLossChance > 0) {
			sock = new PacketLossUDPSocket(sock, packetLossChance);
		}
		if (ping > 0) {
			sock = new HighPingUDPSocket(sock, ping);
		}
		return sock;
	}
}
