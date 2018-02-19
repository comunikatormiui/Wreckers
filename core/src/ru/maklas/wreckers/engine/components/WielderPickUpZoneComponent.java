package ru.maklas.wreckers.engine.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import ru.maklas.mengine.Component;
import ru.maklas.wreckers.assets.EntityType;

public class WielderPickUpZoneComponent implements Component{

    public final FixtureDef def;
    public Fixture fixture;

    public WielderPickUpZoneComponent(FixtureDef def) {
        this.def = def;
    }

    public WielderPickUpZoneComponent(Shape shape) {
        def = new FixtureDef();
        def.isSensor = true;
        def.shape = shape;
        def.filter.categoryBits = EntityType.PLAYER_PICKUP.category;
        def.filter.maskBits = EntityType.PLAYER_PICKUP.mask;
    }

    public boolean enabled(){
        return fixture != null;
    }
}
