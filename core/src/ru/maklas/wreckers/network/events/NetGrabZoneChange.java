package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class NetGrabZoneChange implements Copyable {
    
    int entityId;
    boolean enable;
    
    public NetGrabZoneChange (int entityId, boolean enable) {
        this.entityId = entityId;
        this.enable = enable;
    }
    
    public NetGrabZoneChange () {
        
    }
    
    public NetGrabZoneChange setAndRet(int entityId, boolean enable) {
        this.entityId = entityId;
        this.enable = enable;
        return this;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    public boolean getState() {
        return this.enable;
    }
    
    
    
    @Override
    public String toString() {
        return "NetGrabZoneChange{" +
        "entityId=" + entityId +
        ", enable=" + enable +
        '}';
    }
    
    @Override
    public Object copy() {
        return new NetGrabZoneChange(entityId, enable);
    }
}
