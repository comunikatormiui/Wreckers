package ru.maklas.wreckers.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.wreckers.libs.ClassUtils;
import ru.maklas.wreckers.libs.Utils;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {




    public static void main(String[] args){


        Vector2 vel = new Vector2(1, -4).nor();
        Vector2 nor = new Vector2(-1, 0).nor();
        float angle = vel.angle(nor);
        angle = angle < 0 ? -angle : angle;
        angle -= 90;
        angle = angle < 0 ? -angle : angle;
        float directPercent = angle / 90f;
        float sharpPercent = 1 - directPercent;
        System.out.println("Sharp:  " + Utils.floatFormatted(sharpPercent  * 100, 2) + '%');
        System.out.println("Direct: " + Utils.floatFormatted(directPercent * 100, 2) + '%');


        //System.out.println(angle);

        if (true){
            return;
        }

        ClassUtils.StringCountResult stringCountResult =
                ClassUtils.countStrings(
                        "ru.maklas.wreckers",
                        false,
                        false,
                        false);

        System.out.println(stringCountResult);


    }
}
