package ru.maklas.wreckers.engine.other;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.*;
import ru.maklas.wreckers.assets.ImageAssets;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.rendering.CameraComponent;
import ru.maklas.wreckers.engine.rendering.CameraSystem;
import ru.maklas.wreckers.mnw.MNW;
import ru.maklas.wreckers.utils.StringUtils;
import ru.maklas.wreckers.utils.TimeSlower;
import ru.maklas.wreckers.utils.Utils;

/**
 * <li><b>Requires:</b> cam, batch
 * <li><b>Subscribes:</b> none
 * <li><b>Emits:</b> none
 * <li><b>Description:</b> Для поверхностного дебага во время игры. Утилиты управления камерой и другое.
 */
public class EntityDebugSystem extends RenderEntitySystem {

	private ImmutableArray<Entity> entities;
	private BitmapFont font;
	private Batch batch;
	private OrthographicCamera cam;
	private static final float range = 30;
	private TextureRegion entityCircle;
	boolean paused = false;
	boolean highlightEntities = false;
	boolean help = false;
	float defaultZoom;
	float zoomBeforePause = 1;
	Color color = Color.WHITE;
	Array<EntitySystem> pausedSystems = new Array<>();
	boolean wasUsingRuler = false;
	boolean isUsingRuler = false;
	Vector2 rulerStart = new Vector2();
	Vector2 rulerEnd = new Vector2();

	String[][] helps = {
			{"H", "Help"},
			{"P", "Pause/Unpause"},
			{"K", "Enable/Disable Entity highlight"},
			{"M", "Change camera mode"},
			{"Z", "Zoom in"},
			{"X", "Zoom out"},
			{"C", "Revert zoom"},
	};

	@Override
	public void onAddedToEngine(Engine engine) {
		entities = engine.getEntities();
		font = addDisposable(new BitmapFont());
		font.setUseIntegerPositions(false);
		cam = engine.getBundler().get(B.cam);
		batch = engine.getBundler().get(B.batch);

		int intRange = (int) range;
		entityCircle = ImageAssets.createImage(intRange * 2, intRange * 2, Color.CYAN, p ->{
			p.drawCircle(intRange, intRange, intRange);
			p.drawCircle(intRange, intRange, intRange - 1);
			p.drawCircle(intRange, intRange, intRange - 2);
		});
		defaultZoom = cam.zoom;
	}

	public EntityDebugSystem setColor(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public void render() {
		font.setColor(color);

		updateCamera();
		updateTimeline();
		updateHelp();
		updateRuler();
		updateZoom();
		updateEntities();
		if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

		if (highlightEntities){
			drawCirclesOnEntities();
		}

		updateHighlightOnMouse();

		if (help) drawHelp();
		if (isUsingRuler) drawRuler();
	}

	private void updateHighlightOnMouse() {
		if (highlightEntities) {
			try {
				Vector2 mouse = Utils.toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
				float rangeSquared = range * range;
				for (Entity entity : entities) {
					if (Vector2.dst2(mouse.x, mouse.y, entity.x, entity.y) < rangeSquared) {
						printEntity(entity);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateEntities() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.K)){
			highlightEntities = !highlightEntities;
		}
	}

	private void updateRuler() {
		isUsingRuler = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
		if (!wasUsingRuler && isUsingRuler){
			rulerStart.set(Utils.getMouse(cam));
			rulerEnd.set(rulerStart);
		} else if (wasUsingRuler && isUsingRuler){
			rulerEnd.set(Utils.getMouse(cam));
		}

		wasUsingRuler = isUsingRuler;
	}

	private void updateZoom() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.Z)){
			cam.zoom -= 0.1f;
			if (cam.zoom < 0.05f) cam.zoom = 0.05f;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.X)){
			cam.zoom += 0.25f;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.C)){
			cam.zoom = defaultZoom;
		}
	}

	private void drawHelp() {
		float scale = 2f * getSafeCamZoom();

		float x = Utils.camLeftX(cam) + 10 * cam.zoom;
		float y = Utils.camTopY(cam) - 10 * cam.zoom;
		float dy = 16 * scale;

		font.getData().setScale(scale);


		for (int i = 0; i < helps.length; i++) {
			String[] line = helps[i];
			font.draw(batch, line[0] + " - " + line[1], x, y, 10, Align.left, false);
			y -= dy;
		}
	}

	private void updateHelp() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.H)){
			help = !help;
		}
	}
	private void updateTimeline() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
			pauseUnpause();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.U)){
			TimeSlower timeSlower = engine.getBundler().get(B.timeSlower);
			if (timeSlower != null){
				timeSlower.setTargetScale(timeSlower.getTargetScale() / 2f);
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
			TimeSlower timeSlower = engine.getBundler().get(B.timeSlower);
			if (timeSlower != null){
				timeSlower.setTargetScale(1);
			}
		}
	}

	public EntityDebugSystem pauseUnpause(){
		if (!paused){
			pausedSystems.clear();
			pausedSystems.addAll(engine.getSystemManager().getEntitySystems());
			pausedSystems.filter(s -> s.isEnabled() && !(s instanceof RenderEntitySystem) && !(s instanceof CameraSystem))
					.foreach(s -> s.setEnabled(false));
			zoomBeforePause = cam.zoom;
		} else {
			pausedSystems.callAndClear(s -> s.setEnabled(true));
			cam.zoom = zoomBeforePause;
		}
		paused = !paused;
		return this;
	}

	private void updateCamera() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
			ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
			if (cameras.size() == 0) return;
			Entity camEntity = cameras.get(0);
			CameraComponent cc = camEntity.get(M.camera);
			cc.mode = Utils.next(cc.mode);
			MNW.gsm.print(cc.mode, 1f, Color.RED);
		}
	}

	private void printEntity(Entity e) {
		batch.setProjectionMatrix(cam.combined);
		Array<Component> components = e.getComponents();

		float scale = 2f * getSafeCamZoom();

		float x = Utils.camLeftX(cam) + (5 * cam.zoom);
		float y = Utils.camTopY(cam) - (5 * cam.zoom);
		float dy = 16 * scale;

		font.getData().setScale(scale);

		font.draw(batch, "id: "  + e.id + ", type: " + e.type + ", x: " + ff(e.x) + ", y: " + ff(e.y) + ", ang: " + ff(e.getAngle()), x, y, 10, Align.left, false);
		for (Component c : components) {
			y -= dy;
			font.draw(batch, StringUtils.componentToString(c), x, y,10, Align.left, false);
		}
	}

	private void drawCirclesOnEntities(){
		ImmutableArray<Entity> entities = engine.getEntities();
		for (Entity entity : entities) {
			batch.draw(entityCircle, entity.x - range, entity.y - range);
		}
	}

	private void drawRuler() {
		float zoom = getSafeCamZoom();
		batch.setColor(Color.BLUE);
		batch.draw(entityCircle, rulerStart.x - range * zoom, rulerStart.y - range * zoom, entityCircle.getRegionWidth() * zoom, entityCircle.getRegionHeight() * zoom);
		batch.draw(entityCircle, rulerEnd.x - range * zoom, rulerEnd.y - range * zoom, entityCircle.getRegionWidth() * zoom, entityCircle.getRegionHeight() * zoom);
		batch.setColor(Color.WHITE);

		float scale = 2f * zoom;
		float dy = 16 * scale;
		font.getData().setScale(scale);
		font.setColor(Color.RED);

		Vector2 vec = Utils.vec1.set(rulerEnd).sub(rulerStart);
		font.draw(batch, StringUtils.ff(Math.abs(vec.x), 1), rulerEnd.x + 20 * scale, rulerEnd.y, 10, Align.left, false);
		font.draw(batch, StringUtils.ff(Math.abs(vec.y), 1), rulerEnd.x + 20 * scale, rulerEnd.y - dy, 10, Align.left, false);
		font.draw(batch, StringUtils.ff(vec.len(), 1), rulerEnd.x + 20 * scale, rulerEnd.y - dy - dy, 10, Align.left, false);
	}

	private float getSafeCamZoom(){
		return Math.max(0.1f, cam.zoom);
	}

	private String ff(float f){
		return Integer.toString(((int) f));
	}
}
