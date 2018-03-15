package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 16.03.2018
 */
public class NetDetachRequest implements Copyable {
    
    int playerId;
    int weaponId;
    
    public NetDetachRequest (int playerId, int weaponId) {
        this.playerId = playerId;
        this.weaponId = weaponId;
    }
    
    public NetDetachRequest () {
        
    }
    
    public NetDetachRequest setAndRet(int playerId, int weaponId) {
        this.playerId = playerId;
        this.weaponId = weaponId;
        return this;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getWeaponId() {
        return this.weaponId;
    }
    
    
    
    @Override
    public String toString() {
        return "NetDetachRequest{" +
        "playerId=" + playerId +
        ", weaponId=" + weaponId +
        '}';
    }
    
    @Override
    public Object copy() {
        return new NetDetachRequest(playerId, weaponId);
    }
}
