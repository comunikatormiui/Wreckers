package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import ru.maklas.mengine.Component;
import ru.maklas.wreckers.assets.EntityType;

public class WeaponPickUpComponent implements Component {

    public Fixture fixture;
    public final FixtureDef def;
    public final AttachAction attachAction;

    public WeaponPickUpComponent(FixtureDef def, AttachAction attachAction) {
        this.def = def;
        this.attachAction = attachAction;
    }

    public WeaponPickUpComponent(Shape shape, AttachAction attachAction) {
        def = new FixtureDef();
        def.shape = shape;
        def.isSensor = true;
        def.filter.categoryBits = EntityType.WEAPON_PICKUP.category;
        def.filter.maskBits = EntityType.WEAPON_PICKUP.mask;
        this.attachAction = attachAction;
    }

    public boolean enabled(){
        return fixture != null;
    }

}
