package ru.maklas.wreckers.game;

import java.util.Comparator;

public class Weapon {

    public final WeaponType type;
    public final int order;
    private final float damage;
    private int ammo;

    public Weapon(WeaponType type, int order, float damage, int ammo) {
        this.type = type;
        this.order = order;
        this.damage = damage;
        this.ammo = ammo;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void decAmmo(int amount){
        ammo -= amount;
    }

    public void incAmmo(int amount){
        ammo += amount;
    }

    public boolean canDec(int amount){
        return ammo - amount >= 0;
    }

    public float getDamage() {
        return damage;
    }

    public static final Comparator<Weapon> weaponComparator = new Comparator<Weapon>() {
        @Override
        public int compare(Weapon w1, Weapon w2) {
            return w1.order - w2.order;
        }
    };
}
