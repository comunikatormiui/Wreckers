package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float dullDamage;        // Публичный модификатор урона прямых ударов. Нормальные значения - 20..100
    public float sliceDamage;       // Публичный модификатор урона краем оружия.  Нормальные значения - 20..100
    public float pierceDamage;       // Публичный модификатор колющего урона.     Нормальные значения - 20..100
    public float dullAdjustment;    // Скрытый модификатор атаки
    public float sliceAdjustment;   // Скрытый модификатор атаки
    public float pierceAdjustment;  // Скрытый модификатор атаки
    public float hitImpulse;    //Дополнительное отбрасывание.     Нормальные значения - 0..80
    public float disarmAbility; //Влияет на вероятность выбить оружие у врага. Нормальные значения - 0..100
    public float stunAbility; //Влияет на шанс оглушить и время действия оглушения. Нормальные значения - 0..100

    public WeaponComponent(float dullDamage, float sliceDamage, float pierceDamage, float dullAdjustment, float sliceAdjustment, float pierceAdjustment, float hitImpulse, float disarmAbility, float stunAbility) {
        this.dullDamage = dullDamage;
        this.sliceDamage = sliceDamage;
        this.pierceDamage = pierceDamage;
        this.dullAdjustment = dullAdjustment;
        this.sliceAdjustment = sliceAdjustment;
        this.pierceAdjustment = pierceAdjustment;
        this.hitImpulse = hitImpulse;
        this.disarmAbility = disarmAbility;
        this.stunAbility = stunAbility;
    }
}
