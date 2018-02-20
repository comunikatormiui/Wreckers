package ru.maklas.wreckers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.wreckers.client.states.MainMenuState;
import ru.maklas.wreckers.libs.gsm_lib.GameStateManager;
import ru.maklas.wreckers.libs.gsm_lib.MultilayerStateManager;

public class Wreckers extends ApplicationAdapter {
    
    private SpriteBatch batch;
    private GameStateManager gsm;
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        gsm = new MultilayerStateManager(new MainMenuState(), batch);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        if (dt > 0.05f){
            dt = 0.016666667f;
        }
        gsm.update(dt);
    }
    
    @Override
    public void dispose () {
        gsm.dispose();
    }
}
