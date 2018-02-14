package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by maklas on 08.10.2017.
 */

public class GSMMulti implements GSMCommand {

    private final GSMCommand[] commands;

    public GSMMulti(GSMCommand... commands) {
        this.commands = commands;
    }

    @Override
    public void execute(GameStateManager gsm, SpriteBatch batch, Stack<State> states) {
        for (GSMCommand command : commands) {
            command.execute(gsm, batch, states);
        }

    }
}
