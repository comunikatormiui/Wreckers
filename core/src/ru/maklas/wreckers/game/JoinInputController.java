package ru.maklas.wreckers.game;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.SocketComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.network.events.NetDetachRequest;
import ru.maklas.wreckers.network.events.NetGrabZoneChangeRequest;

public class JoinInputController implements InputController {


    private final GameModel model;

    public JoinInputController(GameModel model) {
        this.model = model;
    }

    @Override
    public void enableGrabZone() {
        Entity player = model.getPlayer();
        if (player != null)
            model.getSocket().send(new NetGrabZoneChangeRequest(player.id, true));
    }

    @Override
    public void disableGrabZone() {
        Entity player = model.getPlayer();
        if (player != null)
            model.getSocket().send(new NetGrabZoneChangeRequest(player.id, false));
    }

    @Override
    public void detachWeapon() {
        Entity player = model.getPlayer();
        if (player == null){
            return;
        }
        SocketComponent sc = player.get(Mappers.socketM);
        if (sc == null){
            return;
        }
        WSocket wSocket = sc.firstAttached();
        if (wSocket == null){
            return;
        }
        model.getSocket().send(new NetDetachRequest(player.id, wSocket.attachedEntity.id));
    }
}
