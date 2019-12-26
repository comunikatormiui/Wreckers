package ru.maklas.wreckers.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.utils.gsm_lib.GameStateManager;
import ru.maklas.wreckers.utils.gsm_lib.State;
import ru.maklas.wreckers.utils.physics.Builders;

public class GameModel {

	Engine engine;
	World world;
	Socket socket;
	private GameStateManager gsm;
	private State currentState;
	private OrthographicCamera cam;
	boolean host;
	int skipFrameForUpdate;
	float lastPing;
	Builders builders;

	Entity player;
	Entity opponent;

	public GameModel() {

	}


	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Entity getPlayer() {
		return player;
	}

	public void setPlayer(Entity player) {
		this.player = player;
	}

	public Entity getOpponent() {
		return opponent;
	}

	public void setOpponent(Entity opponent) {
		this.opponent = opponent;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Builders getBuilders() {
		return builders;
	}

	public void setBuilder(Builders builders) {
		this.builders = builders;
	}

	public GameStateManager getGsm() {
		return gsm;
	}

	public void setGsm(GameStateManager gsm) {
		this.gsm = gsm;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	public OrthographicCamera getCam() {
		return cam;
	}

	public float getLastPing() {
		return lastPing;
	}

	public void setPing(float lastPing) {
		this.lastPing = lastPing;
	}

	public void setCamera(OrthographicCamera camera) {
		this.cam = camera;
	}

	public int getSkipFrameForUpdate() {
		return skipFrameForUpdate;
	}

	/**
	 * устанавливает через сколько кадров произройдет апдейт. Напирмер:
	 * 2 будет означать что скипнется текущий фрейм, затем следующий.
	 * И только на после-следующий кадр будет произведен апдейт.
	 * В сумме пропустится времени: 0.016 * 2 = 0.032 секунды
	 * количество пропущенного времени с момента триггера метода:
	 * <p>
	 *	 <li>  1: 0.016</li>
	 *	 <li>  2: 0.032</li>
	 *	 <li>  5: 0.08</li>
	 *	 <li>10: 0.16</li>
	 *	 <li>15: 0.24</li>
	 *	 <li>20: 0.32</li>
	 *	 <li>30: 0.48</li>
	 *	 <li>60: 1</li>
	 * </p>
	 *
	 * @param skipFrameForUpdate
	 */
	public void setSkipFrameForUpdate(int skipFrameForUpdate) {
		this.skipFrameForUpdate = skipFrameForUpdate;
	}

	public void decSkipFrames(){
		this.skipFrameForUpdate--;
	}

	public void updateOnThisFrame(){
		this.skipFrameForUpdate = 0;
	}

	public boolean timeToUpdate(){
		return skipFrameForUpdate <= 0;
	}
}
