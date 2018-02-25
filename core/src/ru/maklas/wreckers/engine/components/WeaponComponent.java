package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float dullDamage;        // ��������� ����������� ����� ������ ������. ���������� �������� - 20..100
    public float sliceDamage;       // ��������� ����������� ����� ����� ������.  ���������� �������� - 20..100
    public float pierceDamage;       // ��������� ����������� �������� �����.     ���������� �������� - 20..100
    public float dullAdjustment;    // ������� ����������� �����
    public float sliceAdjustment;   // ������� ����������� �����
    public float pierceAdjustment;  // ������� ����������� �����
    public float hitImpulse;    //�������������� ������������.     ���������� �������� - 0..80
    public float disarmAbility; //������ �� ����������� ������ ������ � �����. ���������� �������� - 0..100
    public float stunAbility; //������ �� ���� �������� � ����� �������� ���������. ���������� �������� - 0..100

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
