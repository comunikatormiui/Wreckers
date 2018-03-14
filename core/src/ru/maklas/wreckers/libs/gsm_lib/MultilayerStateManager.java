package ru.maklas.wreckers.libs.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author maklas. Created on 11.05.2017.
 */

public class MultilayerStateManager implements GameStateManager {

    private final Stack<State> states;
    private final SpriteBatch batch;
    private boolean useCommand = false;
    private GSMCommand command;

    public MultilayerStateManager(State startingState, SpriteBatch batch){
        this.batch = batch;
        states = new Stack<State>();
        states.push(startingState);
        startingState.inject(this, batch);
        startingState.onCreate();
        InputProcessor ip = startingState.getInput();
        if (ip == null){
            ip = new InputAdapter();
        }
        Gdx.input.setInputProcessor(ip);
    }


    @Override
    public void setCommand(GSMCommand command) {
        useCommand = true;
        this.command = command;
    }


    public void update(float dt){
        try {
            processCommand();

            Stack<State> states = this.states;
            int size = states.size();
            for (int i = 0; i < size; i++) {
                State state = states.get(i);
                if (state.updatable){
                    state.update(dt);
                }
            }

            for (int i = 0; i < size; i++) {
                State state = states.get(i);
                if (state.render){
                    state.render(batch);
                }
            }

            renderMsgs(dt);
            processCommand();
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public void toBackground(){
        Stack<State> states = this.states;
        int size = states.size();
        for (int i = 0; i < size; i++) {
            State state = states.get(i);
            state.toBackground();
        }
    }

    public void toForeground(){
        Stack<State> states = this.states;
        int size = states.size();
        for (int i = 0; i < size; i++) {
            State state = states.get(i);
            state.toForeground();
        }
    }

    private void processCommand() {
        if (useCommand){
            useCommand = false;
            final GSMCommand command = this.command;
            this.command = null;
            command.execute(this, batch, states);
        }
    }

    public State getCurrentState(){
        return states.peek();
    }

    public void printStackTrace(){
        for (int i = states.size() - 1; i >= 0; i--){
            System.out.println("State # " + i + " is " + states.get(i).getClass().getSimpleName());
        }
    }

    @Override
    public State getState(int number) {
        if (number < 0){
            throw new RuntimeException("Can't get you negative state");
        }
        if (number >= states.size()){
            return null;
        }
        return states.get(number);
    }

    public void dispose(){
        for (int i = states.size() - 1; i >= 0; i--) {
            states.get(i).dispose();
        }
        states.clear();
        msgsFont.dispose();
    }




    private final BitmapFont msgsFont = new BitmapFont();
    private final Array<Msg> msgs = new Array<Msg>(true, 16);
    private final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private final int maxMsgs = (Gdx.graphics.getHeight() / 25) - 1;

    private void renderMsgs(float dt) {
        if (msgs.size == 0){
            return;
        }

        SpriteBatch batch = this.batch;
        batch.setProjectionMatrix(camera.combined);

        float x = 10 - Gdx.graphics.getWidth()/2;
        float y = -20 + Gdx.graphics.getHeight()/2;

        Iterator<Msg> iterator = msgs.iterator();

        batch.begin();
        while (iterator.hasNext()) {
            Msg next = iterator.next();
            msgsFont.setColor(next.color);
            msgsFont.draw(batch, next.message.toString(), x, y);
            next.ttl -= dt;
            if (next.ttl < 0){
                iterator.remove();
            }
            y -= 25;
        }
        batch.end();

    }


    @Override
    public void print(Object msg){
        print(msg, 1);
    }

    @Override
    public void print(Object msg, float ttl){
        print(msg, ttl, Color.RED);
    }

    @Override
    public void print(Object msg, float ttl, Color color){
        if (msg == null) msg = "null";
        Msg m = new Msg(ttl, msg, color);
        if (msgs.size > maxMsgs){
            msgs.removeIndex(0);
        }
        msgs.add(m);
    }

    @Override
    public void printAsync(final Object msg, final float ttl) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                print(msg, ttl);
            }
        });
    }

    private class Msg{

        float ttl;
        final Object message;
        final Color color;

        public Msg(float ttl, Object message, Color color) {
            this.ttl = ttl;
            this.message = message;
            this.color = color;
        }
    }




}
