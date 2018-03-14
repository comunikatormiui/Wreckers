package ru.maklas.wreckers.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.GrabZoneComponent;
import ru.maklas.wreckers.engine.components.PickUpComponent;
import ru.maklas.wreckers.engine.components.StatusEffectComponent;
import ru.maklas.wreckers.engine.events.requests.AttachRequest;
import ru.maklas.wreckers.engine.others.DisarmStatusEffect;
import ru.maklas.wreckers.game.fixtures.FixtureData;

/**
 * В дополнении к стандартному ивенту о коллизии двух твердых тел, генерирует ещё и AttachRequest,
 * основываясь на FixtureType и при включенных pickUpZone и grabberZone. Диспатчит ивент другому клиенту
 */
public class HostContactListener extends DefaultContactListener {

    private final GameModel model;

    public HostContactListener(Engine engine, GameModel model) {
        super(engine);
        this.model = model;
    }

    @Override
    public void beginContact(Contact contact) {
        // Sensor Overlap
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.isSensor() && fixtureB.isSensor()) {
            handleBothSensors(contact, fixtureA, fixtureB);
        }
    }

    @SuppressWarnings("all")
    private void handleBothSensors(Contact contact, final Fixture fixtureA, final Fixture fixtureB) {
        FixtureData udA;
        FixtureData udB;
        Entity eA = (Entity) fixtureA.getBody().getUserData();
        Entity eB = (Entity) fixtureB.getBody().getUserData();
        try {
            udA = (FixtureData) fixtureA.getUserData();
            udB = (FixtureData) fixtureB.getUserData();
        } catch (Exception e) {
            System.err.println(eA + " OR " + eB + " have no fixture data on some of their fixtures");
            e.printStackTrace();
            return;
        }
        FixtureType type1 = udA.getFixtureType();
        FixtureType type2 = udB.getFixtureType();

        if (type1 == FixtureType.PICKUP_SENSOR && type2 == FixtureType.GRABBER_SENSOR) {
            final PickUpComponent wPick = eA.get(Mappers.pickUpM);
            final GrabZoneComponent pPick = eB.get(Mappers.grabM);
            StatusEffectComponent secB = eB.get(Mappers.effectM);
            boolean grabberHasNoDisarmStatus = secB == null || !secB.contains(DisarmStatusEffect.class);
            if (wPick.pickUpZoneEnabled() && pPick.enabled() && grabberHasNoDisarmStatus) {
                engine.dispatchLater(new AttachRequest(eB, pPick, eA, wPick));
            }
        } else if (type1 == FixtureType.GRABBER_SENSOR && type2 == FixtureType.PICKUP_SENSOR) {
            final PickUpComponent wPick = eB.get(Mappers.pickUpM);
            final GrabZoneComponent pPick = eA.get(Mappers.grabM);
            StatusEffectComponent secA = eA.get(Mappers.effectM);
            boolean grabberHasNoDisarmStatus = secA == null || !secA.contains(DisarmStatusEffect.class);
            if (wPick.pickUpZoneEnabled() && pPick.enabled() && grabberHasNoDisarmStatus) {
                engine.dispatchLater(new AttachRequest(eA, pPick, eB, wPick));
            }
        }
    }
}
