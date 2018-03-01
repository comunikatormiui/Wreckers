package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.*;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;


/**
 * Component for grabbing this entity.
 */
public class PickUpComponent implements Component {


    public final FixtureDef def; // definition ��� ���� ����������
    @Nullable public Fixture fixture; // ���� ���������� ��������. ���� != null, �� ��� ������� � ������ ������. �� fixture ����� ������� ���� ���������

    public boolean isAttached = false; // ���������� �� � ������ ������. ���� True, �� �����: owner != null && joint != null
    public final AttachAction attachAction; // �������� ��� ������������
    public Entity owner; //������� ��������
    public Joint joint;  //Joint ������� ��������� � ���������� � ������ ������

    public PickUpComponent(FixtureDef def, AttachAction attachAction) {
        this.def = def;
        this.attachAction = attachAction;
    }

    public PickUpComponent(Shape shape, AttachAction attachAction) {
        def = new FixtureDef();
        def.shape = shape;
        def.isSensor = true;
        def.filter.categoryBits = EntityType.WEAPON_PICKUP.category;
        def.filter.maskBits = EntityType.WEAPON_PICKUP.mask;
        this.attachAction = attachAction;
    }

    /**
     * means that this entity has PickUp fixture enabled, != null and can be grabbed
     */
    public boolean pickUpZoneEnabled(){
        return fixture != null;
    }

}
