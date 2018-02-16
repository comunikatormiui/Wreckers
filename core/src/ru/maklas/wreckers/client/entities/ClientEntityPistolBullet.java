package ru.maklas.wreckers.client.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.TTLComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.ShotEvent;
import ru.maklas.wreckers.libs.Utils;

public class ClientEntityPistolBullet extends Entity implements RayCastCallback {

    private final Vector2 destination;
    private final Vector2 force;
    private final float damage;
    private final World world;
    private final Entity shooter;
    private boolean hit = false;

    public ClientEntityPistolBullet(Entity shooter, float x, float y, Vector2 direction, float range, float force, float damage, World world, EntityType eType) {
        super(x, y, GameAssets.bulletZ);
        this.shooter = shooter;
        this.type = eType.type;
        this.destination = new Vector2(direction).setLength(range).add(x, y).scl(1/GameAssets.box2dScale);
        this.force = new Vector2(direction).setLength(force);
        this.damage = damage;
        this.world = world;
        setAngle(direction.angle());
    }


    @Override
    protected void addedToEngine(Engine engine) {
        add(new TTLComponent(0.03f));
        world.rayCast(this, x / GameAssets.box2dScale, y / GameAssets.box2dScale, destination.x, destination.y);
        if (!hit){
            RenderUnit ru = new RenderUnit(Images.line);
            ru.pivotX = 0;
            ru.pivotY = 0.5f;
            add(new RenderComponent(ru));
        }
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        RenderUnit ru = new RenderUnit(Images.line);
        float distanceToPoint = Utils.vec1.set(point).scl(GameAssets.box2dScale).sub(x, y).len();
        ru.width = distanceToPoint < ru.width ? distanceToPoint : ru.width;
        ru.pivotX = 0;
        ru.pivotY = 0.5f;
        add(new RenderComponent(ru));
        hit = true;

        Entity e = (Entity) fixture.getBody().getUserData();
        HealthComponent hc = e.get(Mappers.healthM);
        if ( hc == null){
            return 0;
        }
        if (fixture.getBody().getType() == BodyDef.BodyType.DynamicBody){
            fixture.getBody().applyLinearImpulse(force, point, true);
        }
        if (!hc.dead){
            getEngine().dispatch(new ShotEvent(shooter, this, e, damage, new Vector2(point).scl(GameAssets.box2dScale), normal.angle()));
        }
        return 0;
    }
}
