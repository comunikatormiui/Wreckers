package ru.maklas.wreckers.assets;

import com.esotericsoftware.kryo.Kryo;
import ru.maklas.mnet.Provider;
import ru.maklas.mnet.Serializer;
import ru.maklas.mnet.impl.KryoSerializer;
import ru.maklas.wreckers.server.events.ConnectionRequest;
import ru.maklas.wreckers.server.events.ConnectionResponse;

public class InetAssets {


    public static Kryo newKryo(){
        Kryo kryo = new Kryo();
        kryo.register(ConnectionRequest.class);
        kryo.register(ConnectionResponse.class);
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
