package ru.maklas.wreckers.client;

import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet.Socket;
import ru.maklas.wreckers.client.entities.EntityPlayer;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.FDefBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;

public class GameModel {

    Engine engine;
    World world;
    Socket socket;
    boolean host;
    int skipFrameForUpdate;

    ShapeBuilder shaper;
    FDefBuilder fixturer;
    BodyBuilder builder;

    Entity player;
    EntityPlayer opponent;

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

    public EntityPlayer getOpponent() {
        return opponent;
    }

    public void setOpponent(EntityPlayer opponent) {
        this.opponent = opponent;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ShapeBuilder getShaper() {
        return shaper;
    }

    public void setShaper(ShapeBuilder shaper) {
        this.shaper = shaper;
    }

    public FDefBuilder getFixturer() {
        return fixturer;
    }

    public void setFixturer(FDefBuilder fixturer) {
        this.fixturer = fixturer;
    }

    public BodyBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(BodyBuilder builder) {
        this.builder = builder;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
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
     *     <li>  1: 0.016</li>
     *     <li>  2: 0.032</li>
     *     <li>  5: 0.08</li>
     *     <li>10: 0.16</li>
     *     <li>15: 0.24</li>
     *     <li>20: 0.32</li>
     *     <li>30: 0.48</li>
     *     <li>60: 1</li>
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

    public void setUpdateThisFrame(){
        this.skipFrameForUpdate = 0;
    }

    public boolean timeToUpdate(){
        return skipFrameForUpdate <= 0;
    }
}
