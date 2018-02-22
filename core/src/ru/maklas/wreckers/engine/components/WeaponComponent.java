package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float dullDamage;    //Модификатор урона прямых ударов. Нормальные значения - 20..100
    public float sliceDamage;   //Модификатор урона краем оружия.  Нормальные значения - 20..100
    public float hitImpulse;    //Дополнительное отбрасывание.     Нормальные значения - 0..80
    public float disarmAbility; //Влияет на вероятность выбить оружие у врага. Нормальные значения - 0..100
    public float stunAbility; //Влияет на шанс оглушить и время действия оглушения. Нормальные значения - 0..100

    public WeaponComponent(float dullDamage, float sliceDamage, float hitImpulse, float disarmAbility, float stunAbility) {
        this.dullDamage = dullDamage;
        this.sliceDamage = sliceDamage;
        this.hitImpulse = hitImpulse;
        this.disarmAbility = disarmAbility;
        this.stunAbility = stunAbility;
    }
}
