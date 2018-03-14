package ru.maklas.wreckers.assets;

import com.esotericsoftware.kryo.Kryo;
import ru.maklas.mnet.Provider;
import ru.maklas.mnet.Serializer;
import ru.maklas.mnet.impl.KryoSerializer;
import ru.maklas.wreckers.network.events.*;

public class InetAssets {


    public static final int defaultPort = 1228;
    public static final int defaultBufferSize = 512;
    public static final int defaultClientSocketUpdate = 75;
    public static final int defaultServerSocketUpdate = 100;

    public static Kryo newKryo(){
        Kryo kryo = new Kryo();
        kryo.register(ConnectionRequest.class);
        kryo.register(ConnectionResponse.class);
        kryo.register(BodySyncEvent.class);
        kryo.register(WreckerSyncEvent.class);
        kryo.register(EntityCreationEvent.class);
        kryo.register(NetAttachDetachEvent.class);
        kryo.register(NetHitEvent.class);
        kryo.register(NetGrabZoneChange.class);
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
