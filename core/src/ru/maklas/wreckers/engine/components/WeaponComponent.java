package ru.maklas.wreckers.engine.components;

import ru.maklas.mengine.Component;

public class WeaponComponent implements Component{

    public float dullDamage;    //����������� ����� ������ ������. ���������� �������� - 20..100
    public float sliceDamage;   //����������� ����� ����� ������.  ���������� �������� - 20..100
    public float hitImpulse;    //�������������� ������������.     ���������� �������� - 0..80
    public float disarmAbility; //������ �� ����������� ������ ������ � �����. ���������� �������� - 0..100
    public float stunAbility; //������ �� ���� �������� � ����� �������� ���������. ���������� �������� - 0..100

    public WeaponComponent(float dullDamage, float sliceDamage, float hitImpulse, float disarmAbility, float stunAbility) {
        this.dullDamage = dullDamage;
        this.sliceDamage = sliceDamage;
        this.hitImpulse = hitImpulse;
        this.disarmAbility = disarmAbility;
        this.stunAbility = stunAbility;
    }
}
