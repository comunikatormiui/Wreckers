package ru.maklas.wreckers.game;

public class WeaponAssets {

    public static Weapon createNew(WeaponType type, int level, int ammo){
        switch (type){
            case NONE:
                return new Weapon(WeaponType.NONE, 0, 0, 0, 0, 0, 0);
            case PISTOL:
                return upgradePistol(new Weapon(WeaponType.PISTOL, 1, 0, ammo, 0, 0, 0), level);
            default:
                throw new RuntimeException("Unknown type of weapon" + type);
        }
    }


    public static Weapon upgrade(Weapon weapon, int level){
        switch (weapon.type){
            case NONE:
                return weapon;
            case PISTOL:
                return upgradePistol(weapon, level);
            default:
                throw new RuntimeException("Unknown type of weapon" + weapon.type);
        }
    }







    private static Weapon upgradePistol(Weapon pistol, int level){
        float damage = 10 + level * 3;
        float cooldown = 1f - (level / 15f);
        cooldown = (cooldown < 0.15f) ? 0.15f : cooldown;

        pistol.setDamage(damage);
        pistol.setCooldown(cooldown);
        pistol.setRange(250 + level * 15);
        pistol.setForce(1.75f + (0.1f * level));
        return pistol;
    }

}
