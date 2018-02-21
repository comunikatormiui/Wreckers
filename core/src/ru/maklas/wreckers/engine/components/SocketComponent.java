package ru.maklas.wreckers.engine.components;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;

public class SocketComponent implements Component{

    public final WSocket[] sockets;
    public final EntityType attachedEntityType;

    public SocketComponent(WSocket[] sockets, EntityType attachedEntityType) {
        this.sockets = sockets;
        this.attachedEntityType = attachedEntityType;
    }

    public SocketComponent(int sockets, EntityType attachedEntityType) {
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
}
