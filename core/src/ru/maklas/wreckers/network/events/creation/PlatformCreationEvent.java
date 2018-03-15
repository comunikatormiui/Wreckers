package ru.maklas.wreckers.network.events.creation;

import ru.maklas.wreckers.libs.Copyable;

public class PlatformCreationEvent implements Copyable {

    int id;
    float x;
    float y;
    float width;
    float height;

    public PlatformCreationEvent (int id, float x, float y, float width, float height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public PlatformCreationEvent () {

    }

    public PlatformCreationEvent setAndRet(int id, float x, float y, float width, float height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }


    @Override
    public String toString() {
        return "PlatformCreationEvent{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public Object copy() {
        return new PlatformCreationEvent(id, x, y, width, height);
    }

}
