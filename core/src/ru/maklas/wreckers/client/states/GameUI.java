package ru.maklas.wreckers.client.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.assets.Images;

public class GameUI extends Stage {

    private final Touchpad touchpad;
    private final GameController controller;

    public GameUI(final GameController controller) {
        this.controller = controller;
        Table table = new Table();
        addActor(table);
        int screenWidth = (int) (640 * 1.5f);
        int screenHeight = (int) (360 * 1.5f);
        getViewport().update(screenWidth, screenHeight, true);

        touchpad = new Touchpad(10, Images.touchStyle);
        touchpad.setBounds(15, 15, 200, 200);
        TextureRegionDrawable block = new TextureRegionDrawable(Images.touchBlock);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(block, block, block, Images.font);
        TextButton pickUpButton = new TextButton("Pick up", style);
        TextButton dropButton = new TextButton("Drop", style);
        pickUpButton.setColor(Color.GREEN);
        dropButton.setColor(Color.RED);


        table.add(dropButton);
        table.add().padRight(120);
        table.add(pickUpButton);
        table.add().padLeft(350);
        table.add(touchpad);

        table.setSize(screenWidth, screenHeight);
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
