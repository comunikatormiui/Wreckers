package ru.maklas.wreckers.engine.systems;

import ru.maklas.wreckers.client.GameModel;

public class JoinNetworkSystem extends NetworkSystem {

    public JoinNetworkSystem(GameModel model) {
        super(model);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        updatePlayer();
    }


    private void updatePlayer(){
        if (model.getPlayer() == null){
            return;
        }
        if (model.timeToUpdate()){
            sendSynchWrecker(model.getPlayer());
            model.setSkipFrameForUpdate(5);
        } else {
            model.decSkipFrames();
        }
    }
}
