package ru.maklas.wreckers.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinAssets extends Asset {

	public Skin skin;

	@Override
	protected void loadImpl() throws Exception {
		skin = new Skin();
		BitmapFont font = new BitmapFont();
		skin.add("default", font);
		skin.add("default", new Label.LabelStyle(font, Color.WHITE));
	}

	@Override
	protected void disposeImpl() throws Exception {
		skin.dispose();
	}
}
