package ru.maklas.wreckers.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.events.requests.DetachRequest;
import ru.maklas.wreckers.engine.events.requests.GrabZoneChangeRequest;

public class KeyboardGameInput extends InputAdapter {

    private final InputController input;

    public KeyboardGameInput(InputController input) {
        this.input = input;
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
            input.enableGrabZone();
        } else if (Input.Keys.O == keycode) {
            input.detachWeapon();
        } else if (Input.Keys.R == keycode){
            input.restart();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (Input.Keys.P == keycode) {
            input.disableGrabZone();
        }
        return true;
    }

}
