package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntity;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Masks;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.libs.Timer;

public class EntityPistolBullet extends UpdatableEntity implements RayCastCallback {

    private final Vector2 direction;
    private final float range;
    private final float force;
    private final float damage;
    private final World world;
    private final Masks masks;
    private Timer deathTimer;

    public EntityPistolBullet(float x, float y, Vector2 direction, float range, float force, float damage, World world, Masks masks) {
        super(x, y, GameAssets.bulletZ);
        this.direction = new Vector2(direction);
        this.range = range;
        this.force = force;
        this.damage = damage;
        this.world = world;
        this.masks = masks;
    }


    @Override
    protected void addedToEngine(Engine engine) {
        deathTimer = new Timer(0.07f, new Timer.Action() {
            @Override
            public boolean execute() {
                getEngine().remove(EntityPistolBullet.this);
                return false;
            }
        });

        Vector2 pos = new Vector2(x, y).scl(1/GameAssets.box2dScale);
        Vector2 destination = new Vector2(pos).add(direction.scl(range/GameAssets.box2dScale));
        world.rayCast(this, pos, destination);
        System.out.println(pos.scl(GameAssets.box2dScale) + ", " +  destination.scl(GameAssets.box2dScale));

        direction.setLength(range / GameAssets.box2dScale);
        direction.sub(x/GameAssets.box2dScale, y/GameAssets.box2dScale);
    }

    @Override
    protected void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }

    @Override
    public void update(float dt) {
        deathTimer.update(dt);
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        getEngine().add(new TestEntity(point.x * GameAssets.box2dScale, point.y * GameAssets.box2dScale, 1));
        Entity e = (Entity) fixture.getBody().getUserData();
        HealthComponent hc = e.get(Mappers.healthM);
        if ( hc == null){
            return 0;
        }
        if (!hc.dead){
            getEngine().dispatch(new DamageEvent(this, e, damage));
            fixture.getBody().applyForceToCenter(new Vector2(direction).setLength(force), true);
        }
        System.out.println(point);
        return 0;
    }
}
