package ru.maklas.wreckers.engine.systems;

import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mnet2.NetBatch;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.SocketComponent;
import ru.maklas.wreckers.engine.components.WSocket;
import ru.maklas.wreckers.engine.components.WeaponComponent;

public class HostNetworkSystem extends NetworkSystem {

    ImmutableArray<Entity> weapons;

    public HostNetworkSystem(GameModel model) {
        super(model);
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        weapons = engine.entitiesFor(WeaponComponent.class);
    }

    @Override
    public void update(float dt) {
        if (model.timeToUpdate()){
            sync();
            model.setSkipFrameForUpdate(5);
        } else {
            model.decSkipFrames();
        }
    }


    /**
     * Синхронизуем игрока и все оружия кроме того что сейчас прикреплён к оппоненту
     */
    private void sync(){
        sendSynchWrecker(model.getPlayer());
        final Entity opponent = model.getOpponent();

        if (opponent == null) {
            syncAllWeapons();
            return;
        }

        final SocketComponent sc = opponent.get(Mappers.socketM);
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

    NetBatch batch = new NetBatch();
    private void syncAllWeapons(){

        for (Entity weapon : weapons) {
            sendSynchWeapon(batch, weapon);
        }
        System.out.println(batch.size());
        model.getSocket().send(batch);
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
