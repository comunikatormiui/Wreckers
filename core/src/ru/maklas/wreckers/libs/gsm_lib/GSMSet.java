package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by maklas on 20.06.2017.
 */

public class GSMSet implements GSMCommand{

    private final State oldState;
    private final State newState;

    public GSMSet(State oldState, State newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public void execute(GameStateManager gsm, SpriteBatch batch, Stack<State> states) {
        states.pop();
        oldState.dispose();

        states.push(newState);
        newState.inject(gsm, batch);
        newState.onCreate();
        InputProcessor ip = newState.getInput();
        if (ip == null){
            ip = nullInput;
        }
        Gdx.input.setInputProcessor(ip);
    }
}
