package ru.maklas.wreckers.libs;

/**
 * Created by maklas on 06-Nov-17.
 */

public class Counter {

    private final int min;
    private final int max;
    private int value;

    public Counter(int min, int max) {
        this.min = min;
        this.max = max;
        this.value = min;
    }

    public int next(){
        final int valueToReturn = value++;
        if (value >= max){
            value = min;
        }
        return valueToReturn;
    }

    public int peak(){
        return value;
    }

    public void reset(){
        this.value = min;
    }

}
