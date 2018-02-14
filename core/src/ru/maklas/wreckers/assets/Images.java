package ru.maklas.wreckers.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class Images {

    private static boolean isLoaded = false;



    public static TextureRegion point;
    private static Array<Disposable> disposables = new Array<Disposable>();

    public static void load(){
        if (isLoaded){
            return;
        }


        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fillCircle(25, 25, 15);
        Texture pointTexture = new Texture(pixmap);
        disposables.add(pointTexture);
        point = new TextureRegion(pointTexture);
        pixmap.dispose();

    }

    public static void dispose(){
        if (!isLoaded){
            return;
        }

        for (Disposable disposable : disposables) {
            disposable.dispose();
        }

        disposables.clear();

    }

    public static boolean isIsLoaded(){
        return isLoaded;
    }


}
