package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.SocketComponent;
import ru.maklas.wreckers.engine.components.WSocket;

public class JoinNetworkSystem extends NetworkSystem {

    public JoinNetworkSystem(GameModel model) {
        super(model);
    }


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
    }

    @Override
    public void update(float dt) {
        sync();
    }

    /**
     * Синхронизуем своё тело и свои оружия если имеются
     */
    private void sync(){
        final GameModel model = this.model;
        final Entity player = model.getPlayer();

        if (player == null){
            return;
        }
        if (model.timeToUpdate()){
            sendSynchWrecker(player);
            final SocketComponent sc = player.get(Mappers.socketM);
            for (WSocket socket : sc.sockets) {
                if (socket.attachedEntity != null){
                    sendSynchWeapon(socket.attachedEntity);
                }
            }
            model.setSkipFrameForUpdate(5);
        } else {
            model.decSkipFrames();
        }
    }
}
