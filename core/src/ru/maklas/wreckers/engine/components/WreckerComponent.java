package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WreckerComponent implements Component {

    public float dullArmor; //����� �������������� �� ������� �����
    public float sliceArmor;//����� �������������� �� ������� �����
    public float stunResist;
    public float disarmResist;
    public float stability; // ��������������� ��������������� ������������. �� 0 �� 100

    public WreckerComponent(float dullArmor, float sliceArmor, float stunResist, float disarmResist, float stability) {
        this.dullArmor = dullArmor;
        this.sliceArmor = sliceArmor;
        this.stunResist = stunResist;
        this.disarmResist = disarmResist;
        this.stability = stability;
    }


}
