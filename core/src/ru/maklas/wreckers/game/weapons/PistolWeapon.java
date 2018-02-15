package ru.maklas.wreckers.game.weapons;

import ru.maklas.wreckers.game.Weapon;
import ru.maklas.wreckers.game.WeaponType;

public class PistolWeapon extends Weapon {

    public PistolWeapon(int ammo) {
        super(WeaponType.PISTOL, 1, 10, ammo);
    }

}
