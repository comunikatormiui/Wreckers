package ru.maklas.wreckers.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

/**
 * Created by maklas on 16.09.2017.
 */

public class Utils {

    public static final Vector2 vec1 = new Vector2(0, 0);
    public static final Vector2 vec2 = new Vector2(0, 0);
    public static final Vector3 vec3 = new Vector3(0, 0, 0);
    public static final Array arr = new Array();
    public static final Random rand = new Random();
    private static final Vector2 localVec = new Vector2(0, 0);

    public static Vector2 toScreen(float x, float y, OrthographicCamera cam) {
        return toScreen(localVec.set(x, y), cam);
    }

    public static Vector2 toScreen(Vector2 vec, OrthographicCamera cam){
        float realWidth = Gdx.graphics.getWidth();
        float realHeight = Gdx.graphics.getHeight();
        float targetWidth = cam.viewportWidth;
        float targetHeight = cam.viewportHeight;
        float zoom = cam.zoom;
        float x = vec.x;
        float y = vec.y;
        float widthScale = realWidth/targetWidth;
        float heightScale = realHeight/targetHeight;

        // aligning x and y to be 0 in the left-bot corner and targetWidth and height and top-right
        x /= widthScale;
        y = (realHeight - y)/heightScale;

        // making x and y coordinates from the center of the screen
        x -= targetWidth/2;
        y -= targetHeight/2;

        // set zoom factor
        x *= zoom;
        y *= zoom;

        // adding camera position to x and y
        x += cam.position.x;
        y += cam.position.y;

        vec.set(x, y);

        return vec;
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> e){
        T[] enumConstants = e.getEnumConstants();
        return enumConstants[rand.nextInt(enumConstants.length)];
    }

    public static String floatFormatted(float f, int numbersAfterComma){
        return String.format(Locale.ENGLISH, "%.0"+ numbersAfterComma + "f", f);
    }

    public static String floatFormatted(float f){
        if (f < 3){
            return floatFormatted(f, 1);
        } else {
            return Integer.toString(Math.round(f));
        }
    }

    public static String priceFormatted(@NotNull String price, char splitter){
        if (price.length() <= 3){
            return price;
        }

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (int i = price.length() - 1; i >= 0; i--) {
            builder.append(price.charAt(i));
            counter++;
            if (counter == 3 && i != 0){
                builder.append(splitter);
                counter = 0;
            }
        }

        return builder.reverse().toString();
    }

   /* public static String intFormatted(long value){

        String l = Long.toString(value);
        int length = l.length();
        if (length < 4){
            return l;
        }

        char splitter = '\'';
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length / 3; i++) {
            int beginIndex = length - ((i + 1) * 3);
            int endIndex = beginIndex + 3;
            beginIndex = beginIndex < 0 ? 0 : beginIndex;
            if (beginIndex != 0){
                builder.append(splitter);
            }
            builder.append(l.substring(beginIndex, endIndex));
        }
        return builder.toString();
    }*/

    public static String addSpacesRight(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String addSpacesLeft(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        builder.append(s);
        return builder.toString();
    }
    /**
     * Пытается найти локальный адрес в сети, который не 127.0.0.1
     * Возвращает null, если не был найден
     * @return
     */
    public static String getLocalInetAddress(){

        try {
            InetAddress address = InetAddress.getLocalHost();
            String localHost = address.getHostAddress();
            if (!localHost.contains("127.0.0.1") && !localHost.contains("::1")){
                return localHost;
            }

                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()){
                NetworkInterface intef = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = intef.getInetAddresses();

                while (inetAddresses.hasMoreElements()){
                    InetAddress a = inetAddresses.nextElement();
                    String addr = a.getHostAddress();
                    boolean isIPv6 = addr.split(":").length > 1;
                    if (!addr.contains("127.0.0.1") && !isIPv6){
                        return addr;
                    }
                }
            }

        } catch (Exception e) {}

        return null;
    }



    public static String getTimeFormatted(int seconds){
        StringBuilder builder = new StringBuilder();
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds);
        int secs = seconds % 60;

        if (hours > 0){
            builder.append(hours).append("h ");
        }

        if ((minutes > 0)){
            builder.append(minutes % 60).append("m ");
        }

        builder.append(secs).append("s");

        return builder.toString();
    }

    public static String getTimeFormattedSign(int seconds, String sign){
        StringBuilder builder = new StringBuilder();
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds);
        int secs = seconds % 60;

        builder.append(addZeros(hours, 2)).append(sign);
        builder.append(addZeros(minutes % 60, 2)).append(sign);
        builder.append(addZeros(secs, 2));

        return builder.toString();
    }

    public static String getTimeShort(int seconds){
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds) % 60;
        int secs = seconds % 60;

        if (hours > 0){
            return hours + " hrs";
        }

        if (minutes > 0){
            return minutes + " min";
        }

        return secs + " sec";
    }






    //******************//
    // Приватные методы //
    //******************//


    private static String addZeros(long number, int size){
        String numberAsString = Long.toString(number);
        if (numberAsString.length() >= size){
            return numberAsString;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size - numberAsString.length(); i++) {
            builder.append("0");
        }
        builder.append(numberAsString);
        return builder.toString();
    }

    private static int getMinutes(int seconds){
        return seconds/60;
    }

    private static int getHours(int seconds){
        return seconds/3600;
    }

    public static String wrapString(String s, int maxCharactersPerLine){
        if (s.length() < maxCharactersPerLine){
            return s;
        }

        String[] split = s.split(" ");
        if (split.length < 2){
            return s;
        }
        StringBuilder builder = new StringBuilder();
        int currentLineSize;

        builder.append(split[0]);
        currentLineSize = split[0].length();
        int i = 1;
        while (i < split.length){
            String word = split[i];

            if (currentLineSize == 0){
                builder.append(word);
                currentLineSize = word.length();
                i++;
                continue;
            }


            if (currentLineSize + word.length() > maxCharactersPerLine){
                builder.append('\n');
                currentLineSize = 0;
            } else {
                builder.append(" ").append(word);
                currentLineSize += word.length() + 1;
                i++;
            }


        }

        return builder.toString();

    }

}
