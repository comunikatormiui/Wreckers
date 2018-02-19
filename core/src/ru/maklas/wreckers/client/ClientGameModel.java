package ru.maklas.wreckers.client;

import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mnet.Socket;
import ru.maklas.wreckers.client.entities.EntityPlayer;
import ru.maklas.wreckers.game.BodyBuilder;
import ru.maklas.wreckers.game.FDefBuilder;
import ru.maklas.wreckers.game.ShapeBuilder;

public class ClientGameModel {

    Engine engine;
    World world;
    Entity player;
    Socket socket;

    ShapeBuilder shaper;
    FDefBuilder fixturer;
    BodyBuilder builder;
    private EntityPlayer opponent;

    public ClientGameModel() {

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
}
