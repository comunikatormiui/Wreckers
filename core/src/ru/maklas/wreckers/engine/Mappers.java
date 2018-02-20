package ru.maklas.wreckers.engine;


import ru.maklas.mengine.ComponentMapper;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.components.rendering.AnimationComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;

/**
 * @author maklas. Created on 05.06.2017.
 */

public class Mappers {

    public static final ComponentMapper<RenderComponent>                renderM = ComponentMapper.of(RenderComponent.class);
    public static final ComponentMapper<CameraComponent>                cameraM = ComponentMapper.of(CameraComponent.class);
    public static final ComponentMapper<AnimationComponent>             animationM = ComponentMapper.of(AnimationComponent.class);
    public static final ComponentMapper<PhysicsComponent>               physicsM = ComponentMapper.of(PhysicsComponent.class);
    public static final ComponentMapper<HealthComponent>                healthM = ComponentMapper.of(HealthComponent.class);
    public static final ComponentMapper<TTLComponent>                   ttlM = ComponentMapper.of(TTLComponent.class);
    public static final ComponentMapper<AntiGravComponent>              antiGravM = ComponentMapper.of(AntiGravComponent.class);
    public static final ComponentMapper<SocketComponent>                socketM = ComponentMapper.of(SocketComponent.class);
    public static final ComponentMapper<GrabZoneComponent>              grabM = ComponentMapper.of(GrabZoneComponent.class);
    public static final ComponentMapper<PickUpComponent>                pickUpM = ComponentMapper.of(PickUpComponent.class);
    public static final ComponentMapper<MotorComponent>                 motorM = ComponentMapper.of(MotorComponent.class);
    public static final ComponentMapper<WeaponComponent>                weaponM = ComponentMapper.of(WeaponComponent.class);


    public static void init(){
        ComponentMapper<RenderComponent> renderM = Mappers.renderM;
    }
}
