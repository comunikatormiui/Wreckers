package ru.maklas.wreckers.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.states.JoinState;

import java.awt.*;

public class Join {

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width  = (int) (640 * 1.0f);
        config.height = (int) (360 * 1.0f);
        config.y = -1;
        config.x = Toolkit.getDefaultToolkit().getScreenSize().width - config.width;
        config.title = "Join";
        new LwjglApplication(new Wreckers(new JoinState()), config);
    }

}
