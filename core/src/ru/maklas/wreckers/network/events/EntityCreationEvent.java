package ru.maklas.wreckers.network.events;

import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.game.EntityEnum;
import ru.maklas.wreckers.libs.Copyable;

/**
 * Created by MaklasEventMaker on 14.03.2018
 */
public class EntityCreationEvent implements Copyable {
    
    EntityEnum entity;
    int id;
    float x;
    float y;
    
    public EntityCreationEvent (EntityEnum entity, int id, float x, float y) {
        this.entity = entity;
        this.id = id;
        this.x = x;
        this.y = y;
    }
    
    public EntityCreationEvent () {
        
    }
    
    public EntityCreationEvent setAndRet(EntityEnum entity, int id, float x, float y) {
        this.entity = entity;
        this.id = id;
        this.x = x;
        this.y = y;
        return this;
    }
    
    public EntityEnum getEntity() {
        return this.entity;
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

    public static EntityCreationEvent fromEntity(EntityEnum entityEnum, Entity entity){
        return new EntityCreationEvent(entityEnum, entity.id, entity.x, entity.y);
    }
    
    
    @Override
    public String toString() {
        return "EntityCreationEvent{" +
        "entity=" + entity +
        ", id=" + id +
        ", x=" + x +
        ", y=" + y +
        '}';
    }
    
    @Override
    public Object copy() {
        return new EntityCreationEvent(entity, id, x, y);
    }
}
