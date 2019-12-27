package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.*;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.statics.Game;
import ru.maklas.wreckers.utils.Config;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.StringUtils;

/**
 * Стандартная система для физики. Сопоставляет body.transform() с entity.x, y, angle.
 * Убеждается что при добавлении Entity в Engine, у body будет Entity в userData,
 * а у каждого Fixture будет FixtureData или кинет RuntimeException
 * При удалении из движка Entity, удаляет и Body, потому не нужно удалять Body самостоятельно.
 */
public class PhysicsSystem extends EntitySystem implements EntityListener, ContactListener {

	private ComponentMapper<PhysicsComponent> mapper;
	private World world;
	private ImmutableArray<Entity> entities;
	private Array<PhysicsComponent> toDestroy = new Array<>();

	@Override
	public void onAddedToEngine(final Engine engine) {
		this.world = engine.getBundler().get(B.world);
		world.setContactListener(this);
		world.setContactFilter(new DefaultCollisionFilter());

		entities = engine.entitiesFor(PhysicsComponent.class);
		mapper = ComponentMapper.of(PhysicsComponent.class);
		for (Entity entity : entities) {
			entity.get(mapper).body.setUserData(entity);
		}
		engine.addListener(this);
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		Entity a = (Entity) fA.getBody().getUserData();
		Entity b = (Entity) fB.getBody().getUserData();

		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 point = worldManifold.getPoints()[0].scl(Game.scale);
		Vector2 normal = worldManifold.getNormal();
		dispatch(new CollisionEvent(a, b, fA, fB, point, normal));
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		dispatch(new PostCollisionEvent().init(contact, impulse));
	}

	@Override
	public void update(float dt) {
		World world = this.world;
		destroyPendings();

		world.step(dt, 8, 3);

		ComponentMapper<PhysicsComponent> collisionM = mapper;
		for (Entity entity : entities) {
			entity.get(collisionM).updateEntity(entity);
		}
	}

	private void destroyPendings() {
		Array<PhysicsComponent> toDestroy = this.toDestroy;
		if (toDestroy.size > 0) {
			for (PhysicsComponent pc : toDestroy) {
				if (pc.body != null) {
					world.destroyBody(pc.body);
					pc.body = null;
				}
			}
			toDestroy.clear();
		}
	}

	@Override
	public void entityAdded(Entity entity) {
		PhysicsComponent cc = entity.get(M.physics);
		if (cc != null) {
			cc.body.setUserData(entity);
			validateFixtureData(entity, cc.body);
		}
	}

	private void validateFixtureData(Entity entity, Body body){
		Array<Fixture> fixtureList = body.getFixtureList();
		for (Fixture fixture : fixtureList) {
			if (fixture.getUserData() == null){
				throw new RuntimeException(entity.getClass().getSimpleName() + " -- " + entity.toString() + " has fixtures without user data!");
			}
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		PhysicsComponent cc = entity.get(M.physics);
		if (cc != null) {
			toDestroy.add(cc);
		}
	}



	///////////////////////////////////////////////////////////////////////////
	// DEBUG RENDERING
	///////////////////////////////////////////////////////////////////////////

	private OrthographicCamera worldCamera;
	private OrthographicCamera gameCamera;
	private PhysicsDebugRenderer renderer;

	public final PhysicsDebugRenderer getDebugRenderer() {
		if (renderer == null) {
			renderer = new PhysicsDebugRenderer(world, getEngine().getBundler().get(B.cam), getWorldCamera(), Game.scale, true);
		}
		return renderer;
	}

	public final OrthographicCamera getWorldCamera() {
		float scale = Game.scaleReversed;
		if (worldCamera == null) {
			this.gameCamera = getEngine().getBundler().get(B.cam);
			this.worldCamera = new OrthographicCamera(gameCamera.viewportWidth * scale, gameCamera.viewportHeight * scale);
		}
		Vector3 position = gameCamera.position;
		worldCamera.position.set(position.x * scale, position.y * scale, position.z * scale);
		return worldCamera;
	}

	public final void renderDebug() {
		getDebugRenderer().render();
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

}
