package ru.maklas.wreckers.libs;

/**
 * Created by amaklakov on 12.09.2017.
 */
public class SimpleProfiler {

    private static long time;

    public static void start(){
        time = System.nanoTime();
    }


    public static String getTimeAsString(){
        return Double.toString(getTime());
    }

    public static String getTimeAsString(int numbersAfterComma){
        return Utils.floatFormatted((float)getTime(), numbersAfterComma);
    }

    public static double getTime(){
        long diff = System.nanoTime() - time;
        return ((double)(diff))/1000000000d;
    }

    public static double getMS(){
        long diff = System.nanoTime() - time;
        return ((double)(diff))/1000000d;
    }

    public static long getNano(){
        return System.nanoTime() - time;
    }

    public static long getStart(){
        return time;
    }

    public static float getMicro() {
        return ((float)(System.nanoTime() - time)) / 1000;
    }


    public static class Multi{

        public static long start(){
            return System.nanoTime();
        }


        public static String getTimeAsString(long start){
            return Double.toString(getTime(start));
        }

        public static String getTimeAsString(long start, int numbersAfterComma){
            return Utils.floatFormatted((float)getTime(start), numbersAfterComma);
        }

        public static double getTime(long start){
            long diff = System.nanoTime() - start;
            return ((double)(diff))/1000000000d;
        }

        public static double getMS(long start){
            long diff = System.nanoTime() - start;
            return ((double)(diff))/1000000d;
        }

        public static long getNano(long start){
            return System.nanoTime() - start;
        }

        public static long getMicro(long start) {
            return (System.nanoTime() - start) / 1000;
        }


    }
}
