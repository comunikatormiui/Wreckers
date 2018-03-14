package ru.maklas.wreckers.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.CollisionEvent;

/**
 * Стандартный Контакт-листнер для мира.
 * Генерирует CollisionEvent в postSolve();
 * Диспатчит в момент когда world делает step()
 */
public class DefaultContactListener implements ContactListener{

    protected final Engine engine;

    public DefaultContactListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void beginContact(Contact contact) { }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        {   //COLLISION EVENT
            Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
            Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
            CollisionEvent event = new CollisionEvent(a, b, contact, impulse, true);
            engine.dispatch(event);
        }
    }


}
