package ru.maklas.wreckers.tests;


import com.badlogic.gdx.math.Vector2;
import ru.maklas.wreckers.engine.components.WreckerComponent;
import ru.maklas.wreckers.engine.systems.HostCollisionSystem;
import ru.maklas.wreckers.libs.ClassUtils;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.network.events.creation.WeaponCreationEvent;
import ru.maklas.wreckers.network.events.creation.WreckerCreationEvent;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {




    public static void main(String[] args){

        new EventMaker()
                .name("HammerCreationEvent")
                .extends_(WeaponCreationEvent.class)
                .build();

        new EventMaker()
                .name("ScytheCreationEvent")
                .extends_(WeaponCreationEvent.class)
                .build();

        if (true){
            return;
        }

        Vector2 vel = new Vector2(0, -3).nor();
        Vector2 playerNorm = new Vector2(-1.1f, 0).nor();
        System.out.println(vel.angle(playerNorm));

        float dullPercent = HostCollisionSystem.calculateDullness(vel, playerNorm);
        float sharpPercent = 1 - dullPercent;
        System.out.println("Sharp:  " + Utils.floatFormatted(sharpPercent  * 100, 2) + '%');
        System.out.println("Direct: " + Utils.floatFormatted(dullPercent * 100, 2) + '%');


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
