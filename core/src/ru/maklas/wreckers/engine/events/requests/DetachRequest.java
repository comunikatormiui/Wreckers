package ru.maklas.wreckers.engine.events.requests;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;

public class DetachRequest {


    public enum Type {
        /**
         * ������� ����� ������ ������ ���� ��� �������
         */
        FIRST,
        /**
         * ������� ��������� ������. � ����� ������ weapon != null
         */
        TARGET}

    Entity wielder;
    @Nullable Entity weapon;
    Type type;

    public DetachRequest(Entity wielder, Type type, @Nullable Entity weapon) {
        this.wielder = wielder;
        this.weapon = weapon;
        this.type = type;
    }

    public Entity getWielder() {
        return wielder;
    }

    @Nullable
    public Entity getWeapon() {
        return weapon;
    }

    public Type getType() {
        return type;
    }
}
