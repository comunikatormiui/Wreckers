package ru.maklas.wreckers.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Consumer;
import com.badlogic.gdx.utils.Disposable;

public class ImageAssets extends Asset{

	public TextureRegion empty;

	/** Green point */
	public TextureRegion point;

	/** Yellow line. Width = 100; Height = 6; */
	public TextureRegion line;
	public TextureRegion arrow;

	public TextureRegion sword;
	public TextureRegion hammer;
	public TextureRegion scythe;
	public BitmapFont font;

	public TextureRegion touchBlock;
	public TextureRegion touchBg;
	public TextureRegion touchKnob;
	public Touchpad.TouchpadStyle touchStyle;

	/** Health bars **/
	public TextureRegion healthBarBorder;
	public TextureRegion healthBarFiller;

	private Array<Disposable> disposables = new Array<>();

	@Override
	protected void loadImpl() {
		empty = new TextureRegion(new Texture("default.png"));
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
		touchBg	= new TextureRegion(new Texture("ui/touchBackground.png"));
		touchKnob  = new TextureRegion(new Texture("ui/touchKnob.png"));
		touchStyle = new Touchpad.TouchpadStyle(new TextureRegionDrawable(touchBg), new TextureRegionDrawable(touchKnob));

		healthBarBorder = new TextureRegion(new Texture("healthBarBorder.png"));
		healthBarFiller = new TextureRegion(new Texture("healthBarFiller.png"));
	}

	@Override
	protected void disposeImpl() {
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
		disposables.clear();
	}

	private static TextureRegion[] split(String path, int horizontal, int vertical) {
		return split(path, horizontal, vertical, horizontal * vertical);
	}

	private static TextureRegion[] split(String path, int horizontal, int vertical, int total){
		Texture texture = new Texture(path);
		TextureRegion[] regions = new TextureRegion[total];
		int width = texture.getWidth() / vertical;
		int height = texture.getHeight() / horizontal;

		int id = 0;
		for (int i = 0; i < horizontal; i++) {
			for (int j = 0; j < vertical; j++) {
				int x = width * j;
				int y = height * i;
				regions[id++] = new TextureRegion(texture, x, y, width, height);
				if (id >= total) return regions;
			}
		}
		return regions;
	}


	public static TextureRegion createCircleImage(int radius, Color color){
		int size = radius * 2 + 2;
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
		pixmap.fill();
		pixmap.setColor(color);
		pixmap.fillCircle(radius + 1, radius + 1, radius);
		return new TextureRegion(new Texture(pixmap));
	}

	public static TextureRegion createCircleImageNoFill(int radius, Color color){
		int size = radius * 2 + 2;
		Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.drawCircle(radius + 1, radius + 1, radius);
		return new TextureRegion(new Texture(pixmap));
	}

	public static TextureRegion createImage(int width, int height, Color color, Consumer<Pixmap> pixmapConsumer){
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmapConsumer.accept(pixmap);
		return new TextureRegion(new Texture(pixmap));
	}

	public static TextureRegion createRectangleImage(int width, int height, Color color){
		Pixmap pixmap = new Pixmap(width + 2, height + 2, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(1, 1, width, height);
		return new TextureRegion(new Texture(pixmap));
	}


	public static TextureRegion createRectangleImage(int width, int height, Color color, Color borderColor){
		Pixmap pixmap = new Pixmap(width + 2, height + 2, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(1, 1, width, height);
		pixmap.setColor(borderColor);
		pixmap.drawRectangle(1, 1, width,  height);
		return new TextureRegion(new Texture(pixmap));
	}
}
