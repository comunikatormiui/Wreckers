package ru.maklas.wreckers.engine.events;

import ru.maklas.mengine.Entity;

/**
 * Ивент об удачной смене состояния оружия (Прикрепилось / Открепилось)
 */
public class AttachEvent {

    private Entity wielder;
    private Entity weapon;
    private boolean attached;

    public AttachEvent(Entity wielder, Entity weapon, boolean attached) {
        this.wielder = wielder;
        this.weapon = weapon;
        this.attached = attached;
    }

    public Entity getWielder() {
        return wielder;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public boolean isAttached() {
        return attached;
    }
}
