package ru.maklas.wreckers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.tests.JoinState;

public class Join {

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width  = (int) (640 * 1.5f);
        config.height = (int) (360 * 1.5f);
        new LwjglApplication(new Wreckers(new JoinState()), config);
        Gdx.graphics.setTitle("Join");
    }

}
