package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 16.02.2018
 */
public class ConnectionResponse implements Copyable {
    
    boolean success;
    String error;
    
    public ConnectionResponse (boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    
    public ConnectionResponse () {
        
    }
    
    public ConnectionResponse setAndRet(boolean success, String error) {
        this.success = success;
        this.error = error;
        return this;
    }
    
    public boolean getSuccess() {
        return this.success;
    }
    
    public String getError() {
        return this.error;
    }
    
    
    
    @Override
    public String toString() {
        return "ConnectionResponse{" +
        "success=" + success +
        ", error=" + error +
        '}';
    }
    
    @Override
    public Object copy() {
        return new ConnectionResponse(success, error);
    }
}
