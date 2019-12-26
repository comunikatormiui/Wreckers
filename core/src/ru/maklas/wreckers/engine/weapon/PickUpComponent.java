package ru.maklas.wreckers.engine.weapon;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Shape;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.statics.EntityType;


/** Component for grabbing this entity **/
public class PickUpComponent implements Component {

	public final FixtureDef def; // definition для зоны подбирания
	@Nullable public Fixture fixture; // зона подбирания предмета. Если != null, то она активна в данный момент. Из fixture можно достать этот компонент

	public boolean isAttached = false; // Присоеденён ли в данный момент. если True, то верно: owner != null && joint != null
	public final AttachAction attachAction; // Действие для прикрепления
	public Entity owner; //Текущий владелец
	public Joint joint;  //Joint который соеденяет с владельцем в данный момент

	public PickUpComponent(FixtureDef def, AttachAction attachAction) {
		this.def = def;
		this.attachAction = attachAction;
	}

	public PickUpComponent(Shape shape, AttachAction attachAction) {
		def = new FixtureDef();
		def.shape = shape;
		def.isSensor = true;
		def.filter.categoryBits = EntityType.of(EntityType.WEAPON_PICKUP).category;
		def.filter.maskBits = EntityType.of(EntityType.WEAPON_PICKUP).mask;
		this.attachAction = attachAction;
	}

	/** means that this entity has PickUp fixture enabled, != null and can be grabbed **/
	public boolean pickUpZoneEnabled(){
		return fixture != null;
	}

}
