package ru.maklas.wreckers.engine;


import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.UpdatableEntitySystem;
import ru.maklas.wreckers.engine.health.HealthComponent;
import ru.maklas.wreckers.engine.health.HostDamageSystem;
import ru.maklas.wreckers.engine.movemnet.AntiGravComponent;
import ru.maklas.wreckers.engine.movemnet.AntiGravSystem;
import ru.maklas.wreckers.engine.movemnet.MotorComponent;
import ru.maklas.wreckers.engine.movemnet.MotorSystem;
import ru.maklas.wreckers.engine.networking.NetworkSystem;
import ru.maklas.wreckers.engine.other.EntityDebugSystem;
import ru.maklas.wreckers.engine.other.FrameTrackSystem;
import ru.maklas.wreckers.engine.other.TTLComponent;
import ru.maklas.wreckers.engine.other.TTLSystem;
import ru.maklas.wreckers.engine.physics.PhysicsComponent;
import ru.maklas.wreckers.engine.physics.PhysicsSystem;
import ru.maklas.wreckers.engine.rendering.*;
import ru.maklas.wreckers.engine.status_effects.StatusEffectComponent;
import ru.maklas.wreckers.engine.status_effects.StatusEffectSystem;
import ru.maklas.wreckers.engine.weapon.PickUpSystem;
import ru.maklas.wreckers.engine.weapon.GrabZoneComponent;
import ru.maklas.wreckers.engine.weapon.PickUpComponent;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.wrecker.WSocketComponent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Маппинг компонентов и последовательность работы систем
 * @author maklas. Created on 05.06.2017.
 */
public class M {

	public static final int totalComponents = 30;

	public static final ComponentMapper<RenderComponent>				render = ComponentMapper.of(RenderComponent.class);
	public static final ComponentMapper<CameraComponent>				camera = ComponentMapper.of(CameraComponent.class);
	public static final ComponentMapper<AnimationComponent>				anim = ComponentMapper.of(AnimationComponent.class);
	public static final ComponentMapper<PhysicsComponent>				physics = ComponentMapper.of(PhysicsComponent.class);
	public static final ComponentMapper<HealthComponent>				health = ComponentMapper.of(HealthComponent.class);
	public static final ComponentMapper<TTLComponent>					ttl = ComponentMapper.of(TTLComponent.class);
	public static final ComponentMapper<AntiGravComponent>				antiGrav = ComponentMapper.of(AntiGravComponent.class);
	public static final ComponentMapper<WSocketComponent>				wSocket = ComponentMapper.of(WSocketComponent.class);
	public static final ComponentMapper<GrabZoneComponent>				grab = ComponentMapper.of(GrabZoneComponent.class);
	public static final ComponentMapper<PickUpComponent>				pickUp = ComponentMapper.of(PickUpComponent.class);
	public static final ComponentMapper<MotorComponent>					motor = ComponentMapper.of(MotorComponent.class);
	public static final ComponentMapper<WeaponComponent>				weapon = ComponentMapper.of(WeaponComponent.class);
	public static final ComponentMapper<WreckerComponent>				wrecker = ComponentMapper.of(WreckerComponent.class);
	public static final ComponentMapper<StatusEffectComponent>			effect = ComponentMapper.of(StatusEffectComponent.class);


	public static void init(){
		int mappers = getMappersReflection() + 2;
		Engine.TOTAL_COMPONENTS = Math.max(mappers, totalComponents);
		ObjectMap<Class<? extends EntitySystem>, Integer> map = Engine.systemOrderMap;
		int i = 1;

		//input
		map.put(FrameTrackSystem.class, i++);
		map.put(NetworkSystem.class, i++);
		map.put(MotorSystem.class, i++);

		//update
		map.put(TTLSystem.class, i++);
		map.put(AnimationSystem.class, i++);
		map.put(PickUpSystem.class, i++);
		map.put(PhysicsSystem.class, i++);
		map.put(AntiGravSystem.class, i++);
		map.put(UpdatableEntitySystem.class, i++);
		map.put(StatusEffectSystem.class, i++);

		//render
		map.put(CameraSystem.class, i++);
		map.put(RenderingSystem.class, i++);
		map.put(EntityDebugSystem.class, i++);
	}

	/** Устанавливаем длинну массива Entity.components[] в зависимости от количества компонентов. **/
	private static int getMappersReflection(){
		int counter = 0;
		try {
			Field[] fields = M.class.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers()) && ComponentMapper.class.isAssignableFrom(field.getType())){
					counter++;
				}
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return counter;
	}

}
