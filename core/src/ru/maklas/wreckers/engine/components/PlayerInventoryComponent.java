package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;
import ru.maklas.wreckers.game.Bag;
import ru.maklas.wreckers.game.Weapon;

public class PlayerInventoryComponent implements Component{

    public final Bag bag;
    public Weapon currentWeapon;

    public PlayerInventoryComponent(Bag bag, Weapon currentWeapon) {
        this.bag = bag;
        this.currentWeapon = currentWeapon;
    }
}
