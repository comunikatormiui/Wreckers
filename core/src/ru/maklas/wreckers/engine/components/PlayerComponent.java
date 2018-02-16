package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;
import ru.maklas.wreckers.game.Bag;
import ru.maklas.wreckers.game.Weapon;

public class PlayerComponent implements Component {

    public float experience;
    public final Bag bag;
    public Weapon currentWeapon;

    public PlayerComponent(float experience, Bag bag, Weapon currentWeapon) {
        this.experience = experience;
        this.bag = bag;
        this.currentWeapon = currentWeapon;
    }
}
