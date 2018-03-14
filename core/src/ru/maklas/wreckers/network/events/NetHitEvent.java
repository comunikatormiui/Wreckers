package ru.maklas.wreckers.network.events;

import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class NetHitEvent implements Copyable {
    
    int playerId;
    int weaponId;
    float x;
    float y;
    float damage;
    float newHealth;
    boolean died;
    float sliceness;
    float dullness;
    float sharpness;
    float stunDuration;

    public NetHitEvent (int playerId, int weaponId, float x, float y, float damage, float newHealth, boolean died, float sliceness, float dullness, float sharpness, float stunDuration) {
        this.playerId = playerId;
        this.weaponId = weaponId;
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.newHealth = newHealth;
        this.died = died;
        this.sliceness = sliceness;
        this.dullness = dullness;
        this.sharpness = sharpness;
        this.stunDuration = stunDuration;
    }
    
    public NetHitEvent () {
        
    }
    
    public NetHitEvent setAndRet(int playerId, int weaponId, float x, float y, float damage, float newHealth, boolean died, float sliceness, float dullness, float sharpness, float stunDuration) {
        this.playerId = playerId;
        this.weaponId = weaponId;
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.newHealth = newHealth;
        this.died = died;
        this.sliceness = sliceness;
        this.dullness = dullness;
        this.sharpness = sharpness;
        this.stunDuration = stunDuration;
        return this;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getWeaponId() {
        return this.weaponId;
    }
    
    public float getDamage() {
        return this.damage;
    }
    
    public float getNewHealth() {
        return this.newHealth;
    }
    
    public boolean died() {
        return this.died;
    }
    
    public float getSliceness() {
        return this.sliceness;
    }
    
    public float getDullness() {
        return this.dullness;
    }
    
    public float getSharpness() {
        return this.sharpness;
    }

    /**
     * If <0 there is no stun
     */
    public float getStunDuration() {
        return stunDuration;
    }

    public boolean doStun(){
        return stunDuration > 0;
    }

    /**
     * Engine scale X coll position
     */
    public float getX() {
        return x;
    }

    /**
     * Engine scale Y coll position
     */
    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "NetHitEvent{" +
        "playerId=" + playerId +
        ", weaponId=" + weaponId +
        ", x=" + x +
        ", y=" + y +
        ", damage=" + damage +
        ", newHealth=" + newHealth +
        ", died=" + died +
        ", sliceness=" + sliceness +
        ", dullness=" + dullness +
        ", sharpness=" + sharpness +
        '}';
    }
    
    @Override
    public Object copy() {
        return new NetHitEvent(playerId, weaponId, x, y, damage, newHealth, died, sliceness, dullness, sharpness, stunDuration);
    }
}
