package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by maklas on 06.10.2017.
 */

public class GSMBackToFirst implements GSMCommand {

    @Override
    public void execute(GameStateManager gsm, SpriteBatch batch, Stack<State> states) {
        if (states.size() == 1){
            return;
        }
        State secondState = null;
        while (states.size() > 1){
            State popped = states.pop();
            secondState = popped;
            popped.dispose();
        }

        State peek = states.peek();
        peek.setUpdatable(true);
        peek.setRender(true);
        InputProcessor input = peek.getInput();
        Gdx.input.setInputProcessor(input == null ? nullInput : input);
        peek.resume(secondState);
    }



}
