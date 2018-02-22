package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WreckerComponent implements Component {

    public float dullArmor; //Защита от прямых тяжелых ударов.  Рассчитывается по формуле лолки. Нормальные значения - 20..300
    public float sliceArmor;//Защита от ударов краем оружия.    Рассчитывается по формуле лолки. Нормальные значения - 20..300
    public float stunResist;//Защита от стана.                  Рассчитывается по формуле лолки. Нормальные значения - 20..300
    public float disarmResist;//Защита от потери оружия при сильном ударе. Формула лолки.        Нормальные значения - 20..300
    public float stability; // Противодействие дополнительному отталкиванию. Формула лолки.      Нормальные значения - 20..300

    public WreckerComponent(float dullArmor, float sliceArmor, float stunResist, float disarmResist, float stability) {
        this.dullArmor = dullArmor;
        this.sliceArmor = sliceArmor;
        this.stunResist = stunResist;
        this.disarmResist = disarmResist;
        this.stability = stability;
    }


}
