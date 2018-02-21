package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.*;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.AntiGravComponent;
import ru.maklas.wreckers.engine.components.PhysicsComponent;
import ru.maklas.wreckers.engine.events.AttachEvent;
import ru.maklas.wreckers.libs.Utils;

public class AntiGravSystem extends EntitySystem {

    ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(AntiGravComponent.class);

        subscribe(new Subscription<AttachEvent>(AttachEvent.class) {
            @Override
            public void receive(Signal<AttachEvent> signal, AttachEvent e) {
                AntiGravComponent antiGrav = Mappers.antiGravM.get(e.getOwner());
                ComponentMapper<PhysicsComponent> physicsM = Mappers.physicsM;
                PhysicsComponent weaponPC = e.getAttachable().get(physicsM);

                if (antiGrav == null || weaponPC == null){
                    return;
                }

                if (e.isAttached()){
                    antiGrav.mass += weaponPC.body.getMass();
                } else {
                    antiGrav.mass -= weaponPC.body.getMass();
                }
            }
        });
    }

    @Override
    public void update(float dt) {
        for (Entity player : entities) {
            AntiGravComponent antiGrav = player.get(Mappers.antiGravM);
            if (!antiGrav.antiGravEnabled){
                continue;
            }

            PhysicsComponent pc = player.get(Mappers.physicsM);
            if (pc != null){
                Vector2 force = Utils.vec1;
                force.set(0, 9.8f * antiGrav.mass);
                if (antiGrav.randomMovementEnabled){
                    changeDeltas(antiGrav, dt);
                    force.add(antiGrav.dX, antiGrav.dY);
                }
                pc.body.applyForceToCenter(force, true);
            }
        }
    }


    void changeDeltas(AntiGravComponent antiGrav, float dt){
        if (antiGrav.directionUp){
            antiGrav.dY += antiGrav.changeSpeed * dt;

            if (antiGrav.dY > antiGrav.maxY){
                antiGrav.directionUp = false;
            }
        } else {
            antiGrav.dY -= antiGrav.changeSpeed * dt;

            if (antiGrav.dY < -antiGrav.maxY){
                antiGrav.directionUp = true;
            }
        }

        if (antiGrav.directionRight){
            antiGrav.dX += antiGrav.changeSpeed * dt;

            if (antiGrav.dX > antiGrav.maxX){
                antiGrav.directionRight = false;
            }
        } else {
            antiGrav.dX -= antiGrav.changeSpeed * dt;

            if (antiGrav.dX < -antiGrav.maxX){
                antiGrav.directionRight = true;
            }
        }
    }
}
