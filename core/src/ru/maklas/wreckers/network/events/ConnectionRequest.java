package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 16.02.2018
 */
public class ConnectionRequest implements Copyable {
    
    String name;
    String version;
    
    public ConnectionRequest (String name, String version) {
        this.name = name;
        this.version = version;
    }
    
    public ConnectionRequest () {
        
    }
    
    public ConnectionRequest setAndRet(String name, String version) {
        this.name = name;
        this.version = version;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    
    
    @Override
    public String toString() {
        return "ConnectionRequest{" +
        "name=" + name +
        ", version=" + version +
        '}';
    }
    
    @Override
    public Object copy() {
        return new ConnectionRequest(name, version);
    }
}
