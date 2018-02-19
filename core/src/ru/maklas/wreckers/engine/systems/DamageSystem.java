package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.physics.box2d.Contact;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.engine.events.CollisionEvent;

import static ru.maklas.wreckers.assets.EntityType.*;

public class DamageSystem extends EntitySystem{

    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(new Subscription<CollisionEvent>(CollisionEvent.class) {
            @Override
            public void receive(Signal<CollisionEvent> signal, CollisionEvent e) {
                EntityType typeA = fromType(e.getA().type);
                EntityType typeB = fromType(e.getB().type);

                if (isPlayerOrOpponent(typeA) && isPlayerOrOpponent(typeB)){
                    handlePlayerToPlayer(e.getA(), e.getB(), e.getContact());

                } else if (isWeapon(typeA) && isWeapon(typeB)){
                    handleWeaponToWeapon(e.getA(), e.getB(), e.getContact());

                } else if (isPlayerOrOpponent(typeA) && isWeapon(typeB)){
                    handleWeaponToPlayer(e.getB(), e.getA(), e.getContact());

                } else if (isPlayerOrOpponent(typeB) && isWeapon(typeA)){
                    handleWeaponToPlayer(e.getA(), e.getB(), e.getContact());

                }
            }
        });

    }


    private void handleWeaponToWeapon(Entity weaponA, Entity weaponB, Contact contact){

    }

    private void handleWeaponToPlayer(Entity weapon, Entity player, Contact contact){

    }

    private void handlePlayerToPlayer(Entity playerA, Entity playerB, Contact contact){

    }

}
