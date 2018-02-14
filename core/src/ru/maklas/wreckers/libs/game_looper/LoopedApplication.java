package ru.maklas.wreckers.libs.game_looper;

/**
 * Created by maklas on 19.08.2017.
 */

public interface LoopedApplication {

    void onStart(LooperAccessor fps);

    void update(float dt);

    void dispose();

}
