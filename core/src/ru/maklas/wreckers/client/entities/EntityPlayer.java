package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.UpdatableEntity;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Masks;
import ru.maklas.wreckers.engine.components.CollisionComponent;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.PlayerInventoryComponent;
import ru.maklas.wreckers.engine.components.ShooterComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.EntityDamageListener;
import ru.maklas.wreckers.engine.events.EntityDeathListener;
import ru.maklas.wreckers.game.*;
import ru.maklas.wreckers.game.weapons.PistolWeapon;
import ru.maklas.wreckers.libs.Utils;

public class EntityPlayer extends UpdatableEntity {

    private Body body;
    private final float relativeShootingX = 15;
    private final float relativeShootingY = -20;

    private final Vector2 shootingPoint = new Vector2();

    public EntityPlayer(int id, float x, float y, float health, Box2dModel model, Masks masks) {
        super(x, y, GameAssets.playerZ);
        this.id = id;
        type = masks.type;

        ShapeBuilder shaper = model.getShapeBuilder();
        BodyBuilder builder = model.getBodyBuilder();

        FixtureDef bodyF = model.getfDefBuilder().newFixture()
            .shape(shaper.buildCircle(0, 0, 20))
                .friction(0.4f)
                .mask(masks)
                .build();

        FixtureDef leftArmF = model.getfDefBuilder().newFixture()
            .shape(shaper.buildCircle(0, 22, 10))
                .friction(0.4f)
                .mask(masks)
                .build();

        FixtureDef rightArmF = model.getfDefBuilder().newFixture()
            .shape(shaper.buildCircle( 0, -22, 10))
                .friction(0.4f)
                .mask(masks)
                .build();

        body = builder.newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(5)
                .addFixture(bodyF)
                .addFixture(leftArmF)
                .addFixture(rightArmF)
                .build();

        add(new CollisionComponent(body));
        add(new HealthComponent(health));
        add(new ShooterComponent(relativeShootingX, relativeShootingY));
        Weapon pistol = new PistolWeapon(50);
        Array<Weapon> weapons = new Array<Weapon>();
        weapons.add(pistol);
        add(new PlayerInventoryComponent(new Bag(weapons), pistol));

    }

    @Override
    protected void addedToEngine(final Engine engine) {
        engine.subscribe(DeathEvent.class, new EntityDeathListener(this) {
            @Override
            public void process(Signal<DeathEvent> signal, DeathEvent deathEvent) {
                engine.remove(EntityPlayer.this);
                signal.remove(this);
            }
        });

        engine.subscribe(DamageEvent.class, new EntityDamageListener(this) {
            @Override
            public void process(Signal<DamageEvent> signal, DamageEvent damageEvent) {
                System.out.println("Player got damaged by: " + damageEvent.getDamageDealer());
            }
        });

    }

    @Override
    public void update(float v) {

    }


    public void lookAt(float x, float y){
        GameAssets.rotateBody(body, x, y);
    }

    public Vector2 getShootingPoint() {
        return shootingPoint;
    }

    public Vector2 getFaceDirection(){
        return body.getTransform().getOrientation();
    }

    public void moveForward(int force) {
        body.applyForceToCenter(Utils.vec1.set(body.getTransform().getOrientation()).scl(force), true);
    }

    public float distanceTo(float x, float y){
        return Utils.vec2.set(body.getPosition()).scl(GameAssets.box2dScale).dst(x, y);
    }
}
