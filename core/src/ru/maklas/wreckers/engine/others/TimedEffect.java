package ru.maklas.wreckers.engine.others;

public abstract class TimedEffect extends StatusEffect {

    protected float ttl;

    public TimedEffect(int priority, float seconds) {
        super(priority);
        this.ttl = seconds;
    }

    @Override
    public final void update(float dt) {
        ttl -= dt;
        if (ttl < 0) {
            removeSelf();
            return;
        }
    }

    public void onUpdate(float dt){}

}
