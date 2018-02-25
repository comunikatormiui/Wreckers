package ru.maklas.wreckers.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class Images {

    private static boolean isLoaded = false;
    private static Array<Disposable> disposables = new Array<Disposable>();


    /**
     * Green point
     */
    public static TextureRegion point;

    /**
     * Yellow line. Width = 100; Height = 6;
     */
    public static TextureRegion line;
    public static TextureRegion arrow;

    public static TextureRegion sword;
    public static TextureRegion hammer;
    public static TextureRegion scythe;
    public static BitmapFont font;

    public static TextureRegion touchBlock;
    public static TextureRegion touchBg;
    public static TextureRegion touchKnob;
    public static Touchpad.TouchpadStyle touchStyle;

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


        pixmap = new Pixmap(104, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fillRectangle(2, 2, 100, 6);
        Texture lineTexture = new Texture(pixmap);
        disposables.add(lineTexture);
        line = new TextureRegion(lineTexture);
        pixmap.dispose();

        Texture swordT = new Texture("sword.png");
        disposables.add(swordT);
        sword = new TextureRegion(swordT);
        Texture hammerT = new Texture("hammer.png");
        disposables.add(hammerT);
        hammer = new TextureRegion(hammerT);
        Texture scytheT = new Texture("Ghost Scythe.png");
        disposables.add(scytheT);
        scythe = new TextureRegion(scytheT);

        font = new BitmapFont();
        disposables.add(font);


        Texture arrowT = new Texture("arrow.png");
        disposables.add(arrowT);
        arrow = new TextureRegion(arrowT);



        touchBlock = new TextureRegion(new Texture("ui/block.png"));
        touchBg    = new TextureRegion(new Texture("ui/touchBackground.png"));
        touchKnob  = new TextureRegion(new Texture("ui/touchKnob.png"));
        touchStyle = new Touchpad.TouchpadStyle(new TextureRegionDrawable(touchBg), new TextureRegionDrawable(touchKnob));
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
