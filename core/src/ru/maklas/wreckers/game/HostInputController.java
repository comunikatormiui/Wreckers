package ru.maklas.wreckers.game;

import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;
import ru.maklas.wreckers.libs.gsm_lib.GSMSet;
import ru.maklas.wreckers.network.events.state_change.NetRestartEvent;
import ru.maklas.wreckers.tests.HostGameState;

public class HostInputController implements InputController {

    private final GameModel model;

    public HostInputController(GameModel model) {
        this.model = model;
    }

    @Override
    public void enableGrabZone() {
        model.getEngine().dispatch(new GrabZoneChangeRequest(true, model.getPlayer()));
    }

    @Override
    public void disableGrabZone() {
        model.getEngine().dispatch(new GrabZoneChangeRequest(false, model.getPlayer()));
    }

    @Override
    public void detachWeapon() {
        model.getEngine().dispatch(new DetachRequest(DetachRequest.Type.FIRST, model.getPlayer(), null));
    }

    @Override
    public void restart() {
        model.getSocket().send(new NetRestartEvent());
        model.getGsm().setCommand(new GSMSet(model.getCurrentState(), new HostGameState(model.getSocket())));
    }
}
