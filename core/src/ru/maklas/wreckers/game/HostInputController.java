package ru.maklas.wreckers.game;

import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;

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
}
