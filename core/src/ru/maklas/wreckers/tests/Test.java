package ru.maklas.wreckers.tests;


import ru.maklas.wreckers.libs.ClassUtils;

/**
 * Created by maklas on 04-Jan-18.
 */

public class Test {




    public static void main(String[] args){

        ClassUtils.StringCountResult stringCountResult =
                ClassUtils.countStrings(
                        "ru.maklas.wreckers",
                        false,
                        false,
                        false);

        System.out.println(stringCountResult);


    }
}
