package ru.maklas.wreckers.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.rendering.CameraComponent;
import ru.maklas.wreckers.statics.EntityType;
import ru.maklas.wreckers.statics.ID;
import ru.maklas.wreckers.statics.Layers;

public class EntityUtils {

	public static Entity camera(OrthographicCamera cam) {
		return new Entity(ID.camera, EntityType.CAMERA, 0, 0, Layers.camera).add(new CameraComponent(cam));
	}
	public static Entity camera(OrthographicCamera cam, int followId) {
		return new Entity(ID.camera, EntityType.CAMERA, 0, 0, Layers.camera).add(new CameraComponent(cam).setFollowEntity(followId, true, true));
	}
}
