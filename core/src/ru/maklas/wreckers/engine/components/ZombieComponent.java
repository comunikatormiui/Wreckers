package ru.maklas.wreckers.engine.components;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

public class ZombieComponent implements Component {

    public float searchCD;
    public float searchTimer;
    @Nullable
    public Entity target;

    public ZombieComponent(float searchCD) {
        this.searchCD = searchCD;
        this.searchTimer = searchCD;
    }

}
