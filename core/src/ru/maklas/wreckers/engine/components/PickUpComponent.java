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
 * Component for grabbing this entity.
 */
public class PickUpComponent implements Component {

    @Nullable public Fixture fixture;
    public final FixtureDef def;
    public final AttachAction attachAction;
    /**
     * If current entity is attached to socket
     */
    public boolean attached = false;
    /**
     * Current owner of the entity
     */
    @Nullable public Entity wielder;

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
    public boolean enabled(){
        return fixture != null;
    }

}
