package ru.maklas.wreckers.engine.components;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;

public class WeaponSocketComponent implements Component{

    public final WSocket[] sockets;

    public WeaponSocketComponent(WSocket[] sockets) {
        this.sockets = sockets;
    }

    public WeaponSocketComponent(int sockets) {
        this.sockets = new WSocket[sockets];
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
}
