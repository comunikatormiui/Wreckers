package ru.maklas.wreckers.assets;

import com.esotericsoftware.kryo.Kryo;
import ru.maklas.mnet.Provider;
import ru.maklas.mnet.Serializer;
import ru.maklas.mnet.impl.KryoSerializer;
import ru.maklas.wreckers.network.events.BodySyncEvent;
import ru.maklas.wreckers.network.events.ConnectionRequest;
import ru.maklas.wreckers.network.events.ConnectionResponse;
import ru.maklas.wreckers.network.events.WreckerSyncEvent;

public class InetAssets {


    public static final int defaultPort = 1228;
    public static final int defaultBufferSize = 512;

    public static Kryo newKryo(){
        Kryo kryo = new Kryo();
        kryo.register(ConnectionRequest.class);
        kryo.register(ConnectionResponse.class);
        kryo.register(BodySyncEvent.class);
        kryo.register(WreckerSyncEvent.class);
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
