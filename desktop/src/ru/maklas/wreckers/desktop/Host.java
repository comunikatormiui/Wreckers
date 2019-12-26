package ru.maklas.wreckers.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.states.HostState;

public class Host {

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width  = (int) (640 * 1.5f);
        config.height = (int) (360 * 1.5f);
        config.y = -1;
        config.x = 0;
        config.title = "Host";
        new LwjglApplication(new Wreckers(new HostState()), config);
    }

}
