package ru.maklas.wreckers.engine;


import ru.maklas.mengine.ComponentMapper;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.components.rendering.AnimationComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;

/**
 * @author maklas. Created on 05.06.2017.
 */

public class Mappers {

    public static final ComponentMapper<VelocityComponent>              velocityM = ComponentMapper.of(VelocityComponent.class);
    public static final ComponentMapper<AccelerationComponent>          accelerationM = ComponentMapper.of(AccelerationComponent.class);
    public static final ComponentMapper<RenderComponent>                renderM = ComponentMapper.of(RenderComponent.class);
    public static final ComponentMapper<CameraComponent>                cameraM = ComponentMapper.of(CameraComponent.class);
    public static final ComponentMapper<AnimationComponent>             animationM = ComponentMapper.of(AnimationComponent.class);
    public static final ComponentMapper<PhysicsComponent>               physicsM = ComponentMapper.of(PhysicsComponent.class);
    public static final ComponentMapper<HealthComponent>                healthM = ComponentMapper.of(HealthComponent.class);
    public static final ComponentMapper<TTLComponent>                   ttlM = ComponentMapper.of(TTLComponent.class);
    public static final ComponentMapper<PlayerComponent>                playerM = ComponentMapper.of(PlayerComponent.class);
    public static final ComponentMapper<AntiGravComponent>              antiGravM = ComponentMapper.of(AntiGravComponent.class);
    public static final ComponentMapper<SocketComponent>          socketM = ComponentMapper.of(SocketComponent.class);
    public static final ComponentMapper<WielderPickUpZoneComponent>          playerPickUpM = ComponentMapper.of(WielderPickUpZoneComponent.class);
    public static final ComponentMapper<WeaponPickUpComponent>          weaponPickUpM = ComponentMapper.of(WeaponPickUpComponent.class);


    public static void init(){
        ComponentMapper<VelocityComponent> velM = Mappers.velocityM;
    }
}
