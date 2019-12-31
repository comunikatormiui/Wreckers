package ru.maklas.wreckers.user_interface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.states.GameController;

public class GameUI extends BaseStage {

	private final Touchpad touchpad;

	public GameUI(final GameController controller) {
		Table table = new Table();
		addActor(table);
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		getViewport().update(screenWidth, screenHeight, true);

		touchpad = new Touchpad(10, A.images.touchStyle);
		touchpad.setBounds(15, 15, 400, 400);
		TextureRegionDrawable block = new TextureRegionDrawable(A.images.touchBlock);
		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(block, block, block, A.images.font);
		TextButton pickUpButton = new TextButton("Pick up", style);
		TextButton dropButton = new TextButton("Drop", style);
		pickUpButton.setColor(Color.GREEN);
		dropButton.setColor(Color.RED);


		table.add(dropButton).bottom();
		table.add().bottom().padRight(120);
		table.add(pickUpButton).bottom();
		table.add().padLeft(800);
		table.add(touchpad).padBottom(100).size(350);

		table.setSize(screenWidth, screenHeight * 0.8f);
		table.align(Align.bottom);
		table.setFillParent(true);

		dropButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.onDropClicked();
			}
		});

		pickUpButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				controller.onAttachDown();
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				controller.onAttachUp();
			}
		});
	}

	public float getTouchX(){
		return touchpad.getKnobPercentX();
	}

	public float getTouchY(){
		return touchpad.getKnobPercentY();
	}
}
