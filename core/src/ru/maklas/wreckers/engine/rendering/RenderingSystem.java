package ru.maklas.wreckers.engine.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.IterableZSortedRenderSystem;
import ru.maklas.wreckers.assets.A;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.health.HealthComponent;
import ru.maklas.wreckers.statics.Layers;
import ru.maklas.wreckers.utils.Config;
import ru.maklas.wreckers.utils.Log;

/** Рендерит все RenderComponent у всех Entity на экран. **/
public class RenderingSystem extends IterableZSortedRenderSystem<RenderComponent> {

	private Batch batch;
	private OrthographicCamera cam;
	private int maxDrawCalls = 0;
	private int currentLayer;
	private static final int minLayer = Integer.MIN_VALUE;
	private static final int maxLayer = Integer.MAX_VALUE;

	public RenderingSystem() {
		super(RenderComponent.class, false);
		setAlwaysInvalidate(true);
	}

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		batch = engine.getBundler().getAssert(B.batch);
		cam = engine.getBundler().getAssert(B.cam);
		maxDrawCalls = 0;
	}

	@Override
	protected void renderStarted() {
		currentLayer = minLayer;
	}

	private void onLayerChanged(int oldLayer, int newLayer) {
		//перечислять строго в порядке повышения слоя.
		// не делать if-else цепочек, так как можно пропустить смену нужного слоя
		if (Layers.playerZ > oldLayer && Layers.playerZ <= newLayer){
			//before rendering player
		}

		if (Layers.playerZ > oldLayer && Layers.playerZ <= newLayer){
			//after rendering player
		}

		if (oldLayer == Layers.playerZ){
			drawHealthBars();
		}
	}

	@Override
	protected void renderEntity(Entity entity, RenderComponent rc) {
		if (!rc.visible) return;
		checkLayerChange(entity.layer);
		Batch batch = this.batch;
		batch.setColor(rc.color);

		int size = rc.renderUnits.size;
		Object[] units = rc.renderUnits.items;

		for (int i = 0; i < size; i++) {
			((RenderUnit) units[i]).draw(batch, entity.x, entity.y, entity.getAngle());
		}
	}

	private static Color mildGreen = new Color(0f, 1f, 0f, 0.2f);
	private static Color mildWhite = new Color(1f, 1f, 1f, 0.2f);
	private void drawHealthBars() {
		float width = 100;
		float height = 8;
		ImmutableArray<Entity> characters = engine.entitiesFor(HealthComponent.class);
		float xOffset = -width / 2f;
		float yOffset = 55;
		for (Entity character : characters) {
			HealthComponent hc = character.get(M.health);
			if (hc != null){
				batch.setColor(hc.isFull() ? mildGreen : Color.GREEN);
				batch.draw(A.images.healthBarFiller, character.x + xOffset, character.y + yOffset, (width * hc.getHealthPercent()), height);
				batch.setColor(hc.isFull() ? mildWhite : Color.WHITE);
				batch.draw(A.images.healthBarBorder, character.x + xOffset, character.y + yOffset, width, height);
			}
		}

	}

	private void checkLayerChange(int newLayer) {
		if (newLayer == currentLayer) return;
		int oldLayer = currentLayer;
		currentLayer = newLayer;
		onLayerChanged(oldLayer, newLayer);
	}

	@Override
	public void onRemovedFromEngine(Engine e) {
		super.onRemovedFromEngine(e);
		batch = null;
		cam = null;
	}

	@Override
	protected void renderFinished() {
		if (Config.LOG_DRAWCALLS && (batch instanceof SpriteBatch || batch instanceof PolygonSpriteBatch)) {
			int renderCalls = batch instanceof SpriteBatch ? ((SpriteBatch) batch).renderCalls : ((PolygonSpriteBatch) batch).renderCalls;
			if (renderCalls > maxDrawCalls) {
				maxDrawCalls = renderCalls;
				Log.trace("RenderingSystem", "Max drawcalls: " + maxDrawCalls);
			}
		}
		onLayerChanged(currentLayer, maxLayer);
		batch.setColor(1, 1, 1, 1);
	}

	public OrthographicCamera getCamera() {
		return cam;
	}
}
