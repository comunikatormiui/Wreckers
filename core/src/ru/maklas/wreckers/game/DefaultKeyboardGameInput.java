package ru.maklas.wreckers.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;

public class DefaultKeyboardGameInput extends InputAdapter {

    protected final GameModel model;

    public DefaultKeyboardGameInput(GameModel model) {
        this.model = model;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //Vector2 realMouse = Utils.toScreen(screenX, screenY, model.getCam());
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        //Vector2 realMouse = Utils.toScreen(screenX, screenY, model.getCam());
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Input.Keys.P == keycode) {
            model.getEngine().dispatch(new GrabZoneChangeRequest(true, model.getPlayer()));
        } else if (Input.Keys.O == keycode) {
            model.getEngine().dispatch(new DetachRequest(DetachRequest.Type.FIRST, model.getPlayer(), null));
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (Input.Keys.P == keycode) {
            model.getEngine().dispatch(new GrabZoneChangeRequest(false, model.getPlayer()));
        }
        return true;
    }

}
