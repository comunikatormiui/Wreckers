package ru.maklas.wreckers.network.events.state_change;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 16.03.2018
 */
public class NetRestartEvent implements Copyable {
    
    
    public NetRestartEvent() {
        
    }
    
    public NetRestartEvent setAndRet() {
        return this;
    }
    
    @Override
    public String toString() {
        return "NetRetryEvent{" +
        '}';
    }
    
    @Override
    public Object copy() {
        return new NetRestartEvent();
    }
}
