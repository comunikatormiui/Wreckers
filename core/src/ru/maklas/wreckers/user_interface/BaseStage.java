package ru.maklas.wreckers.user_interface;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class BaseStage extends Stage implements View {

	@Override
	public void updateStrings() { }

	@Override
	public InputProcessor getInput() {
		return this;
	}

	@Override
	public void resize(int width, int height) {
		getViewport().update(width, height, true);
		getCamera().viewportWidth = width;
		getCamera().viewportHeight = height;
		getCamera().position.set(width / 2f, height / 2f, getCamera().position.z);
		getCamera().update(true);
	}
}
