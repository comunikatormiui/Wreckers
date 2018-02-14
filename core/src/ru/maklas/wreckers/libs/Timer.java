package ru.maklas.wreckers.libs;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by maklas on 19.09.2017.
 */

public class Timer {

    private boolean triggered = false;
    private float timer = 0;
    private float time = 0;
    private Action action;


    public Timer() {

    }

    public Timer(float seconds, Action action) {
        this.action = action;
        this.time = seconds;
    }

    public Timer setAction(Action action){
        this.action = action;
        return this;
    }

    public Timer setTime(float seconds){
        this.time = seconds;
        return this;
    }

    /**
     * Resets time to 0 and enables if Action returned true last time
     */
    public Timer reset(){
        timer = 0;
        triggered = false;
        return this;
    }

    /**
     * Updates timer. Returns true if Action was triggered
     */
    public boolean update(float dt){
        if (!triggered){
            timer+=dt;

            if (timer > time){
                triggered = true;
                if (action != null){
                    if (action.execute()){
                        reset();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Action getAction() {
        return action;
    }

    /**
     * Action that's going to be triggered later in time
     */
    public interface Action {

        /**
         * @return whether we should reset timer and lauch again
         */
        boolean execute();
    }

    public static class Arr implements Iterable<Timer>{

        Array<Timer> timers = new Array<Timer>();

        public void add(Timer timer){
            timers.add(timer);
        }

        public Timer add(){
            Timer timer = new Timer();
            timers.add(timer);
            return timer;
        }

        public void update(float dt){
            for (Timer timer : timers) {
                timer.update(dt);
            }
        }

        public void remove(Timer timer){
            timers.removeValue(timer, true);
        }

        public void removeInactive(){
            Iterator<Timer> iterator = timers.iterator();
            while (iterator.hasNext()){
                if (iterator.next().triggered){
                    iterator.remove();
                }
            }

        }

        @Override
        public Iterator<Timer> iterator() {
            return timers.iterator();
        }
    }

}
