package ru.maklas.wreckers.libs;

import com.badlogic.gdx.Gdx;

import java.io.OutputStream;

public abstract class Log {

    private static final long startTime = System.currentTimeMillis();

    public static Log SERVER = new SoutLogger("Server");
    public static Log CLIENT = new SoutLogger("Client");
    public static Log WARNING = new ErrorStreamLogger("WARNING");

    //FileLogger("tag", "logs.txt");
    //--   чтобы идентифицировать где закоменчены полезные логи


    public abstract void info(Object msg);
    public abstract void warning(Object msg);
    public abstract void event(Object e);
    public abstract void stateTransition(Object a, Object b);
    public abstract void stateTransition(String msg, Object a, Object b);



    public static void sout(String tag, String msg){
        soutWithTime(tag + " -i- " + msg);
    }
    public static void serr(String tag, String msg){
        serrWithTime(tag + " -w- " + msg);
    }

    private static void serrWithTime(String msg){
        System.err.println(getTime() + " " + msg);
    }

    private static void soutWithTime(String msg){
        System.out.println(getTime() + " " + msg);
    }
    private static String getTime(){
        int sinceStart = (int)(System.currentTimeMillis() - startTime);
        return Utils.getTimeFormattedSign(sinceStart/1000, ":");
    }


    public static final class SoutLogger extends Log{

        private final String tag;

        public SoutLogger(String tag) {
            this.tag = tag;
        }

        public void info(Object msg){
            soutWithTime(tag + " -i- " + msg);
        }

        public void warning(Object msg){
            soutWithTime(tag + " -w- " + msg);
        }

        public void event(Object e){
            soutWithTime(tag + " -e- " + e);
        }

        public void stateTransition(Object a, Object b){
            soutWithTime(tag + " -State change- " + a + " -> " + b);
        }

        public void stateTransition(String msg, Object a, Object b){
            soutWithTime(tag + " -State change- " + msg + ": " + a + " -> " + b);
        }


    }

    public static final class RedWarningLogger extends Log{

        private final String tag;

        public RedWarningLogger(String tag) {
            this.tag = tag;
        }

        public void info(Object msg){
            soutWithTime(tag + " -i- " + msg);
        }

        public void warning(Object msg){
            serrWithTime(tag + " -w- " + msg);
        }

        public void event(Object e){
            soutWithTime(tag + " -e- " + e);
        }

        public void stateTransition(Object a, Object b){
            soutWithTime(tag + " -State change- " + a + " -> " + b);
        }


        public void stateTransition(String msg, Object a, Object b){
            soutWithTime(tag + " -State change- " + msg + ": " + a + " -> " + b);
        }
    }

    public static final class SilentLogger extends Log{

        public SilentLogger(String tag) {

        }

        @Override
        public void info(Object msg) {

        }

        @Override
        public void warning(Object msg) {

        }

        @Override
        public void event(Object e) {

        }

        @Override
        public void stateTransition(Object a, Object b) {

        }

        @Override
        public void stateTransition(String msg, Object a, Object b) {

        }
    }

    public static final class ErrorStreamLogger extends Log{

        private final String tag;

        public ErrorStreamLogger(String tag) {
            this.tag = tag;
        }

        public void info(Object msg){
            serrWithTime(tag + " -i- " + msg);
        }

        public void warning(Object msg){
            serrWithTime(tag + " -w- " + msg);
        }

        public void event(Object e){
            serrWithTime(tag + " -e- " + e);
        }

        public void stateTransition(Object a, Object b){
            serrWithTime(tag + " -State change- " + a + " -> " + b);
        }

        public void stateTransition(String msg, Object a, Object b){
            serrWithTime(tag + " -State change- " + msg + ": " + a + " -> " + b);
        }
    }

    public static final class FileLogger extends Log{

        private final String tag;
        private final OutputStream stream;

        public FileLogger(String tag, String fileInAssets) {
            stream = Gdx.files.local(fileInAssets).write(true);
            this.tag = tag;
        }

        public void info(Object msg){
            write(tag + " -i- " + msg);
        }

        public void warning(Object msg){
            write(tag + " -w- " + msg);
        }

        public void event(Object e){
            write(tag + " -e- " + e);
        }

        public void stateTransition(Object a, Object b){
            write(tag + " -State change- " + a + " -> " + b);
        }

        public void stateTransition(String msg, Object a, Object b){
            write(tag + " -State change- " + msg + ": " + a + " -> " + b);
        }

        private void write(String msg){
            try {
                stream.write(msg.getBytes());
                stream.write(Character.getDirectionality('\n'));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
