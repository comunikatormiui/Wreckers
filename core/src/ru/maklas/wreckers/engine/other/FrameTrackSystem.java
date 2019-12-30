package ru.maklas.wreckers.engine.other;

import ru.maklas.mengine.EntitySystem;

/** Just dispatches event when update of the engine begins **/
public class FrameTrackSystem extends EntitySystem {

	private final FrameStartedEvent e = new FrameStartedEvent();

	@Override
	public void update(float dt) {
		dispatch(e);
	}
}
