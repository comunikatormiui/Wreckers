package ru.maklas.wreckers.engine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.components.WeaponComponent;

public class HostNetworkSystem extends NetworkSystem {

    ImmutableArray<Entity> weapons;

    public HostNetworkSystem(GameModel model) {
        super(model);
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        weapons = engine.entitiesFor(WeaponComponent.class);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (model.timeToUpdate()){
            syncAll();
            model.setSkipFrameForUpdate(5);
        } else {
            model.decSkipFrames();
        }
    }


    private void syncAll(){
        sendSynchWrecker(model.getPlayer());
        for (Entity weapon : weapons) {
            sendSynchWeapon(weapon);
        }
    }
}
