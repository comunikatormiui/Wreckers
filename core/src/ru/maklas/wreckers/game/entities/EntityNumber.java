package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntArray;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.other.TTLComponent;
import ru.maklas.wreckers.engine.rendering.RenderComponent;
import ru.maklas.wreckers.engine.rendering.RenderUnit;
import ru.maklas.wreckers.engine.rendering.TextureUnit;

public class EntityNumber extends Entity {

	private static TextureRegion[] numbers = new TextureRegion[10];
	private static TextureRegion minus;
	private static int spaceWidth = 5;
	private static float scale = 2;

	public EntityNumber(int number, float ttl, float x, float y) {
		this(number, ttl, x, y, Color.GREEN);
	}

	public EntityNumber(int number, float ttl, float x, float y, Color color) {
		super(x, y, 100);

		if (numbers[0] == null || !numbers[0].getTexture().isManaged())
			init();

		final boolean appendMinus = number < 0;
		if (appendMinus){
			number = -number;
		}

		IntArray digits = new IntArray();
		while (number != 0){
			digits.add(number % 10);
			number = number / 10;
		}
		digits.shrink();
		digits.reverse();

		float dtX = 0;

		RenderComponent rc = new RenderComponent().color(color);

		for (int item : digits.items) {
			TextureRegion n = numbers[item];
			RenderUnit ru = new TextureUnit(n);
			ru.localX = dtX;
			ru.scaleX = ru.scaleY = scale;
			dtX += n.getRegionWidth() * scale + spaceWidth * scale;
			rc.add(ru);
		}

		if (appendMinus){
			RenderUnit minusRu = new TextureUnit(minus);
			minusRu.scaleX = minusRu.scaleY = scale;
			minusRu.localX = - spaceWidth * scale - minus.getRegionWidth() * scale;
			rc.add(minusRu);
		}


		add(rc);
		add(new TTLComponent(ttl));
	}


	private static void init(){
		BitmapFont font = A.images.font;
		spaceWidth = font.getData().getGlyph(' ').width;
		BitmapFont.Glyph minusGlyph = font.getData().getGlyph('-');
		minus = new TextureRegion(font.getRegion(), minusGlyph.srcX, minusGlyph.srcY, minusGlyph.width, minusGlyph.height);

		for (int i = 0; i < 10; i++) {
			BitmapFont.Glyph glyph = font.getData().getGlyph(Character.forDigit(i, 10));
			TextureRegion region = new TextureRegion(font.getRegion(), glyph.srcX, glyph.srcY, glyph.width, glyph.height);
			numbers[i] = region;
		}
	}
}
