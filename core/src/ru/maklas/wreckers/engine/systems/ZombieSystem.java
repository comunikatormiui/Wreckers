package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.components.PlayerComponent;
import ru.maklas.wreckers.engine.components.ZombieComponent;

public class ZombieSystem extends EntitySystem{

    private ImmutableArray<Entity> zombies;
    private ImmutableArray<Entity> players;
    private Vector2 tempVec = new Vector2();

    @Override
    public void onAddedToEngine(Engine engine) {
        zombies = engine.entitiesFor(ZombieComponent.class);
        players = engine.entitiesFor(PlayerComponent.class);
    }

    @Override
    public void update(float dt) {
        ComponentMapper<ZombieComponent> zombieM = Mappers.zombieM;
        ComponentMapper<PhysicsComponent> physicsM = Mappers.physicsM;
        Vector2 tempVec = this.tempVec;

        for (Entity zombie : zombies) {
            ZombieComponent zc = zombie.get(zombieM);
            zc.searchTimer -= dt;
            if (zc.searchTimer < 0){
                zc.searchTimer = zc.searchCD;
                updateClosestTarget(zombie, zc);
            }

            Entity target = zc.target;
            if (target != null){
                Body body = zombie.get(physicsM).body;
                float velocity = zombie.get(Mappers.velocityM).velocity;
                GameAssets.rotateBody(body, target.x, target.y);
                tempVec.set(body.getTransform().getOrientation()).scl(velocity);
                body.applyForceToCenter(tempVec, true);
            }

        }

    }

    private void updateClosestTarget(Entity zombie, ZombieComponent zc) {

        float maxDistance = 1000000000;

        for (Entity player : players) {
            float distance = Vector2.len(player.x - zombie.x, player.y - zombie.y);
            if (distance < maxDistance){
                maxDistance = distance;
                zc.target = player;
            }
        }
    }

    @Override
    public void removeFromEngine() {
        super.removeFromEngine();
    }
}
