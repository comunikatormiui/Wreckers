package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 * Ивент заставляющий Join отвязать/привязать Оружие
 */
public class NetAttachDetachEvent implements Copyable {
    
    int playerId;
    int weaponId;
    boolean attach;
    
    public NetAttachDetachEvent(int playerId, int weaponId, boolean attach) {
        this.playerId = playerId;
        this.weaponId = weaponId;
        this.attach = attach;
    }
    
    public NetAttachDetachEvent() {
        
    }
    
    public NetAttachDetachEvent setAndRet(int playerId, int weaponId, boolean attach) {
        this.playerId = playerId;
        this.weaponId = weaponId;
        this.attach = attach;
        return this;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getWeaponId() {
        return this.weaponId;
    }
    
    public boolean toAttach() {
        return this.attach;
    }
    
    
    
    @Override
    public String toString() {
        return "NetAttachDetachRequest{" +
        "playerId=" + playerId +
        ", weaponId=" + weaponId +
        ", attach=" + attach +
        '}';
    }
    
    @Override
    public Object copy() {
        return new NetAttachDetachEvent(playerId, weaponId, attach);
    }
}
