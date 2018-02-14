package ru.maklas.wreckers.libs;

import com.badlogic.gdx.utils.AtomicQueue;

public class SynchronizedAtomicQueue<T> extends AtomicQueue<T> {

    public SynchronizedAtomicQueue(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized boolean put(T value) {
        return super.put(value);
    }

    @Override
    public synchronized T poll() {
        return super.poll();
    }
}
