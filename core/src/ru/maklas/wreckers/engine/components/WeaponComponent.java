package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float dullDamage;    //Умножается на импульс
    public float sliceDamage;   //Умножается на импульс
    public float hitImpulse;    // от 0 до 100. Дополнительное отбрасывание
    public float disarmAbility; // от 0 до 100. Влияет на вероятность выбить оружие у врага
    public float stunAbility; // от 0 до 100. Влияет на шанс оглушить и время действия оглушения

    public WeaponComponent(float dullDamage, float sliceDamage, float hitImpulse, float disarmAbility, float stunAbility) {
        this.dullDamage = dullDamage;
        this.sliceDamage = sliceDamage;
        this.hitImpulse = hitImpulse;
        this.disarmAbility = disarmAbility;
        this.stunAbility = stunAbility;
    }
}
