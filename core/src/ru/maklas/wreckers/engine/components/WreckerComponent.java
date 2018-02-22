package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WreckerComponent implements Component {

    public float dullArmor; //������ �� ������ ������� ������.  �������������� �� ������� �����. ���������� �������� - 20..300
    public float sliceArmor;//������ �� ������ ����� ������.    �������������� �� ������� �����. ���������� �������� - 20..300
    public float stunResist;//������ �� �����.                  �������������� �� ������� �����. ���������� �������� - 20..300
    public float disarmResist;//������ �� ������ ������ ��� ������� �����. ������� �����.        ���������� �������� - 20..300
    public float stability; // ��������������� ��������������� ������������. ������� �����.      ���������� �������� - 20..300

    public WreckerComponent(float dullArmor, float sliceArmor, float stunResist, float disarmResist, float stability) {
        this.dullArmor = dullArmor;
        this.sliceArmor = sliceArmor;
        this.stunResist = stunResist;
        this.disarmResist = disarmResist;
        this.stability = stability;
    }


}
