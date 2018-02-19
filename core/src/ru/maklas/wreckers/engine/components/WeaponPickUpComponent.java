package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.Shape;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;

/**
 *  омпонент отвечает за зону в которой можно подн€ть оружие, а так же за текущее соединение с носителем
 */
public class WeaponPickUpComponent implements Component {

    @Nullable public Fixture fixture;
    public final FixtureDef def;
    public final AttachAction attachAction;
    public boolean attached = false;
    @Nullable public Entity wielder;

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

    /**
     * ќзначает что компонент активен и оружие готово быть подобранным
     * @return
     */
    public boolean enabled(){
        return fixture != null;
    }

}
