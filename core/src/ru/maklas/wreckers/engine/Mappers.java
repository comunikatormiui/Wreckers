package ru.maklas.wreckers.engine;


import ru.maklas.mengine.ComponentMapper;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.components.rendering.AnimationComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;

/**
 * @author maklas. Created on 05.06.2017.
 */

public class Mappers {

    public static final ComponentMapper<VelocityComponent>            velocityM = ComponentMapper.of(VelocityComponent.class);
    public static final ComponentMapper<AccelerationComponent>        accelerationM = ComponentMapper.of(AccelerationComponent.class);
    public static final ComponentMapper<RenderComponent>              renderM = ComponentMapper.of(RenderComponent.class);
    public static final ComponentMapper<CameraComponent>              cameraM = ComponentMapper.of(CameraComponent.class);
    public static final ComponentMapper<AnimationComponent>           animationM = ComponentMapper.of(AnimationComponent.class);
    public static final ComponentMapper<CollisionComponent>           collisionM = ComponentMapper.of(CollisionComponent.class);
    public static final ComponentMapper<HealthComponent>              healthM = ComponentMapper.of(HealthComponent.class);
    public static final ComponentMapper<ShooterComponent>             shooterM = ComponentMapper.of(ShooterComponent.class);
    public static final ComponentMapper<PlayerInventoryComponent>     inventoryM = ComponentMapper.of(PlayerInventoryComponent.class);


    public static void init(){
        ComponentMapper<VelocityComponent> velM = Mappers.velocityM;
    }
}
