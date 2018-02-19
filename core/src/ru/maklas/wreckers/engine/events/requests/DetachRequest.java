package ru.maklas.wreckers.engine.events.requests;

import ru.maklas.mengine.Entity;

public class DetachRequest {


    public enum Type {
        /**
         * ������� ����� ������ ������ ���� ��� �������
         */
        FIRST,
        /**
         * ������� ��������� ������ �� ���������� Entity. � ����� ������ weapon != null � wielder != null
         */
        TARGET_ENTITY_AND_WEAPON,
        /**
         * ������������ ��������� ������. wielder ����� ���� null.
         */
        TARGET_WEAPON

    }

    Entity wielder;
    Entity weapon;
    Type type;

    public DetachRequest(Entity wielder, Type type, Entity weapon) {
        this.wielder = wielder;
        this.weapon = weapon;
        this.type = type;
    }

    public Entity getWielder() {
        return wielder;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public Type getType() {
        return type;
    }
}
