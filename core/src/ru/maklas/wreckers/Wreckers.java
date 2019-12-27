package ru.maklas.wreckers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.libs.SimpleProfiler;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.mnw.MNW;
import ru.maklas.wreckers.states.SinglePlayerState;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.gsm_lib.EmptyStateManager;
import ru.maklas.wreckers.utils.gsm_lib.MultilayerStateManager;
import ru.maklas.wreckers.utils.gsm_lib.State;

public class Wreckers extends ApplicationAdapter {

	public static final String VERSION = "0.1";
	private State launchState;
	private Batch batch;

	public Wreckers(State state) {
		this.launchState = state;
	}

	public Wreckers() {
		this(new SinglePlayerState());
	}

	@Override
	public void create () {
		float scale = (float) Gdx.graphics.getWidth() / Game.width;
		Game.height = Math.round((float) Gdx.graphics.getHeight() / scale);
		Game.hHeight = Game.height / 2;

		State launchState = this.launchState;
		this.launchState = null;
		try {
			initialize();
			MNW.gsm.launch(launchState, batch);
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
			MNW.gsm = new EmptyStateManager();
		}
	}

	private void initialize(){
		SimpleProfiler.start();
		batch = new SpriteBatch();
		MNW.gsm = new MultilayerStateManager();
		M.init();

		A.images.load();
		A.skins.load();

		Log.trace("Initialized in " + SimpleProfiler.getTimeAsString());
	}

	@Override
	public void resize(int width, int height) {
		MNW.gsm.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		if (dt > 0.02f){
			dt = 0.016666667f;
		}
		MNW.gsm.update(dt);
	}

	@Override
	public void dispose () {
		MNW.gsm.dispose();
		batch.dispose();
	}
}
