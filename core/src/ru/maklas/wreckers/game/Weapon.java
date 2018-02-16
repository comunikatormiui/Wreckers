package ru.maklas.wreckers.game;

import java.util.Comparator;

public class Weapon {

    public static final Comparator<Weapon> weaponComparator = new Comparator<Weapon>() {
        @Override
        public int compare(Weapon w1, Weapon w2) {
            return w1.order - w2.order;
        }
    };

    public final WeaponType type;
    public final int order;
    private float damage;
    private int ammo;
    private float force;
    private float range;
    private float cooldown;
    private float currentCooldown;

    public Weapon(WeaponType type, int order, float damage, int ammo, float range, float force, float cooldown) {
        this.type = type;
        this.order = order;
        this.damage = damage;
        this.ammo = ammo;
        this.range = range;
        this.force = force;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
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

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getForce() {
        return force;
    }

    public float getRange() {
        return range;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(float currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void decCurrentCooldown(float seconds){
        currentCooldown -= seconds;
        if (currentCooldown < 0){
            currentCooldown = 0;
        }
    }

    public boolean isOnCooldown(){
        return currentCooldown > 0.0001f;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !getClass().isAssignableFrom(other.getClass())) return false;

        Weapon weapon = (Weapon) other;
        return type == weapon.type;
    }

    @Override
    public int hashCode() {
        return type.ordinal() * 1232323;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "type=" + type +
                ", order=" + order +
                ", damage=" + damage +
                ", ammo=" + ammo +
                ", force=" + force +
                ", range=" + range +
                ", cooldown=" + cooldown +
                ", currentCooldown=" + currentCooldown +
                '}';
    }

    public void setCooldownToMax() {
        this.currentCooldown = cooldown;
    }
}
