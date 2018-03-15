package ru.maklas.wreckers.desktop;

import ru.maklas.wreckers.libs.ClassUtils;

public class Both {

    public static void main (String[] arg) throws Exception{
        Host.main(arg);
        Thread.sleep(1000);
        ClassUtils.newProcess(Join.class, arg);
    }


}
