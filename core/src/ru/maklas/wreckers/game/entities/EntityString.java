package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.other.TTLComponent;
import ru.maklas.wreckers.engine.rendering.RenderComponent;
import ru.maklas.wreckers.engine.rendering.RenderUnit;
import ru.maklas.wreckers.engine.rendering.TextureUnit;

public class EntityString extends Entity {

	private static ObjectMap<Character, TextureRegion> charMap = new ObjectMap<Character, TextureRegion>();
	private static TextureRegion questionMark;
	private static int spaceWidth = 5;
	private static float scale = 2;

	public EntityString(String s, int ttl, float x, float y, Color color) {
		super(x, y, 100);
		if (charMap.size < 2){
			init();
		}



		RenderComponent rc = new RenderComponent();
		rc.color = color;


		float dtX = 0;
		for (int i = 0; i < s.length(); i++) {
			TextureRegion n = charMap.get(s.charAt(i));
			if (n == null){
				n = questionMark;
			}
			RenderUnit ru = new TextureUnit(n);
			ru.pivotX = 0;
			ru.pivotY = 0;
			ru.localX = dtX;
			ru.scaleX = ru.scaleY = scale;
			dtX += n.getRegionWidth() * scale + spaceWidth * scale;
			rc.add(ru);
		}


		add(rc);
		add(new TTLComponent(ttl));
	}





	private static void init() {

		BitmapFont font = A.images.font;
		spaceWidth = font.getData().getGlyph(' ').width;

		BitmapFont.Glyph q = font.getData().getGlyph('?');
		questionMark = new TextureRegion(font.getRegion(), q.srcX, q.srcY, q.width, q.height);

		for (int i = 32; i < 256; i++) {
			char c = (char) i;
			if (font.getData().hasGlyph(c)){
				BitmapFont.Glyph glyph = font.getData().getGlyph(c);
				TextureRegion region = new TextureRegion(font.getRegion(), glyph.srcX, glyph.srcY, glyph.width, glyph.height);
				charMap.put(c, region);
			}
		}
	}

}
