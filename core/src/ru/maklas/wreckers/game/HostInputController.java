package ru.maklas.wreckers.game;

import ru.maklas.mengine.Engine;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.weapon.DetachRequest;
import ru.maklas.wreckers.engine.weapon.GrabZoneChangeRequest;
import ru.maklas.wreckers.net_events.state_change.NetRestartEvent;
import ru.maklas.wreckers.states.HostGameState;
import ru.maklas.wreckers.utils.gsm_lib.GSMSet;

public class HostInputController implements InputController {

	private final Engine engine;

	public HostInputController(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void enableGrabZone() {
		engine.dispatch(new GrabZoneChangeRequest(true, engine.getBundler().get(B.player)));
	}

	@Override
	public void disableGrabZone() {
		engine.dispatch(new GrabZoneChangeRequest(false, engine.getBundler().get(B.player)));
	}

	@Override
	public void detachWeapon() {
		engine.dispatch(new DetachRequest(DetachRequest.Type.FIRST, engine.getBundler().get(B.player), null));
	}

	@Override
	public void restart() {
		engine.getBundler().get(B.socket).send(new NetRestartEvent());
		engine.getBundler().get(B.gsmState).getGsm().setCommand(new GSMSet(engine.getBundler().get(B.gsmState), new HostGameState(engine.getBundler().get(B.socket))));
	}
}
