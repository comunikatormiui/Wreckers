package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by maklas on 03-Feb-18.
 */

public class StateController {

    private final State state;

    public StateController(State state) {
        this.state = state;
    }

    protected final void setState(State state){
        this.state.setState(state);
    }

    protected final void popState(){
        this.state.popState();
    }

    protected final void pushState(State state, boolean keepUpdating, boolean keepRendering){
        this.state.pushState(state, keepUpdating, keepRendering);
    }

    protected final <T> void pushPromiseState(boolean keepUpdating, boolean keepRendering, PromiseState<T> state, PromiseHandler<T> handler){
        this.state.pushPromiseState(keepUpdating, keepRendering,  state, handler);
    }

    protected final GameStateManager getGsm() {
        return state.getGsm();
    }

    protected final SpriteBatch getBatch(){
        return state.batch;
    }
}
