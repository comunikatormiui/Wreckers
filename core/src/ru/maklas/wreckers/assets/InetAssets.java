package ru.maklas.wreckers.assets;

import com.esotericsoftware.kryo.Kryo;
import ru.maklas.mnet.Provider;
import ru.maklas.mnet.Serializer;
import ru.maklas.mnet.impl.KryoSerializer;
import ru.maklas.wreckers.network.events.*;
import ru.maklas.wreckers.network.events.creation.*;
import ru.maklas.wreckers.network.events.state_change.NetRestartEvent;
import ru.maklas.wreckers.network.events.sync.BodySyncEvent;
import ru.maklas.wreckers.network.events.sync.WreckerSyncEvent;

public class InetAssets {


    public static final int defaultPort = 1228;
    public static final int defaultBufferSize = 512;
    public static final int defaultClientSocketUpdate = 75;
    public static final int defaultServerSocketUpdate = 100;

    public static Kryo newKryo(){
        Kryo kryo = new Kryo();

        //Connection
        kryo.register(ConnectionRequest.class);
        kryo.register(ConnectionResponse.class);

        //State
        kryo.register(NetRestartEvent.class);

        //Creation
        kryo.register(WreckerCreationEvent.class);
        kryo.register(WeaponCreationEvent.class);
        kryo.register(SwordCreationEvent.class);
        kryo.register(HammerCreationEvent.class);
        kryo.register(ScytheCreationEvent.class);
        kryo.register(PlatformCreationEvent.class);

        //Synchronization
        kryo.register(BodySyncEvent.class);
        kryo.register(WreckerSyncEvent.class);

        //Game Events
        kryo.register(NetAttachDetachEvent.class);
        kryo.register(NetHitEvent.class);
        kryo.register(NetGrabZoneChange.class);
        kryo.register(NetGrabZoneChangeRequest.class);
        kryo.register(NetDetachRequest.class);


        return kryo;
    }

    private static Provider<Serializer> provider = new Provider<Serializer>() {
        @Override
        public Serializer provide() {
            return new KryoSerializer(newKryo(), 512);
        }
    };
    public static Provider<Serializer> serializerProvider(){
        return provider;
    }

}
