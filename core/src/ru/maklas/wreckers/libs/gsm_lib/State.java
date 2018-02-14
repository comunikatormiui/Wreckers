package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

/**
 *  Created on 03.02.2017.
 *  @author maklas
 *
 *  never initialize any variables in update or render method.
 *  Initialize ONLY in "onCreate" method or in constructor if needed
 *
 */
public abstract class State {

    boolean updatable = true;
    boolean render = true;

    protected GameStateManager gsm;
    protected SpriteBatch batch;

    protected HashMap<PromiseState, PromiseHandler> promiseMap = new HashMap<PromiseState, PromiseHandler>();


    void inject(GameStateManager gsm, SpriteBatch batch){
        this.gsm = gsm;
        this.batch = batch;
    }


    abstract protected void onCreate();

    /**
     *  automatically sets input processor upon creation and resuming to the state.
     *  Must be set in order to prevent problems with input system
     *
     * @return InputAdapter/InputMultiplexer/InputProcessor
     */
    protected InputProcessor getInput(){
        return null;
    }


    /**
     * Triggers 60 times a second to update game logic every frame
     * @param dt how much time last last update function took in seconds
     */
    abstract protected void update (float dt);

    abstract protected void render(SpriteBatch batch);

    /**
     *  Triggers after pushing StateManager to the new State
     */
    protected void onPause(){}

    /**
     *  Called every time when in StateManager pop() is called and this State becomes the current again
     *
     *  All inputs must be set back
     */
    protected void onResume(){}


    final void resume(State from){
        if (from == null){
            System.err.println("State.class STATE RESUME ERROR");
            onResume();
            return;
        }

        if (from instanceof PromiseState) {
            PromiseState promiseState = ((PromiseState) from);
            PromiseHandler handler = promiseMap.remove(promiseState);
            if (handler != null) {

                if (handler.triggerAfterResume){
                    onResume();
                    handler.handle(promiseState.getPromise());
                } else {
                    handler.handle(promiseState.getPromise());
                    onResume();
                }

            }
        } else {
            onResume();
        }

    }

    /**
     * Triggers when application in Android goes to background and not visible any more
     */
    protected void toBackground(){}

    /**
     * Triggers when application in Android goes to foreground and is visible again
     */
    protected void toForeground(){}

    abstract protected void dispose();

    /**
     * Determines whether to update this State.
     */
    public void setUpdatable(boolean updatable){
        this.updatable = updatable;
    }

    /**
     * Determines whether to render this State.
     */
    public void setRender(boolean enable){
        this.render = enable;
    }

    /**
     * @return if this state is getting updates from loop
     */
    public final boolean isUpdating(){
        return updatable;
    }

    /**
     * @return if this state is getting rendered
     */
    public final boolean isRendering(){
        return render;
    }

    protected final void setState(State state){
        gsm.setCommand(new GSMSet(this, state));
    }

    protected final void popState(){
        gsm.setCommand(new GSMPop(this));
    }

    protected final void pushState(State state, boolean keepUpdating, boolean keepRendering){
        gsm.setCommand(new GSMPush(this, state, !keepUpdating, !keepRendering));
    }

    protected final <T> void pushPromiseState(boolean keepUpdating, boolean keepRendering, PromiseState<T> state, PromiseHandler<T> handler){
        promiseMap.put(state, handler);
        pushState(state, keepUpdating, keepRendering);
    }

    public GameStateManager getGsm() {
        return gsm;
    }
}
