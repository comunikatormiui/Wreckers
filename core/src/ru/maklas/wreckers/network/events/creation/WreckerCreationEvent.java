package ru.maklas.wreckers.network.events.creation;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.libs.Copyable;
import ru.maklas.wreckers.engine.components.WreckerComponent;

/**
 * Created by MaklasEventMaker on 15.03.2018
 */
public class WreckerCreationEvent implements Copyable {
    
    int id;
    float x;
    float y;
    float angle;
    float health;
    WreckerComponent stats;
    boolean player;
    
    public WreckerCreationEvent (int id, float x, float y, float angle, WreckerComponent stats, float health, boolean player) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.stats = stats;
        this.health = health;
        this.player = player;
    }
    
    public WreckerCreationEvent () {
        
    }
    
    public WreckerCreationEvent setAndRet(int id, float x, float y, float angle, WreckerComponent stats, float health, boolean player) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.stats = stats;
        this.health = health;
        this.player = player;
        return this;
    }

    @Nullable
    public static WreckerCreationEvent fromEntity(Entity e, boolean isPlayer){
        WreckerComponent wc = e.get(Mappers.wreckerM);
        HealthComponent hc = e.get(Mappers.healthM);
        if (wc == null || hc == null){
            return null;
        }
        return new WreckerCreationEvent(e.id, e.x, e.y, 0, (WreckerComponent) wc.copy(), hc.health, isPlayer);
    }
    
    public int getId() {
        return this.id;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }

    public float getAngle() {
        return this.angle;
    }

    public WreckerComponent getStats() {
        return this.stats;
    }

    public float getHealth() {
        return health;
    }

    /**
     * True = player,
     * False = opponent
     */
    public boolean isPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "WreckerCreationEvent{" +
        "id=" + id +
        ", x=" + x +
        ", y=" + y +
        ", angle=" + angle +
        ", stats=" + stats +
        ", player=" + player +
        '}';
    }

    @Override
    public Object copy() {
        return new WreckerCreationEvent(id, x, y, angle, (WreckerComponent) stats.copy(), health, player);
    }
}
