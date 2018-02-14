package ru.maklas.wreckers.engine;

public enum EntityType{

    NONE;

    private static final EntityType[] vals = values();

    public static EntityType fromOrdinal(int ordinal){
        return vals[ordinal];
    }


}
