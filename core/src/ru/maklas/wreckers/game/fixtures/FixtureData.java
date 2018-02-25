package ru.maklas.wreckers.game.fixtures;

import ru.maklas.wreckers.game.FixtureType;

public class FixtureData {

    FixtureType fixtureType;

    public FixtureData(FixtureType fixtureType) {
        this.fixtureType = fixtureType;
    }

    public FixtureType getFixtureType() {
        return fixtureType;
    }

    public void setFixtureType(FixtureType fixtureType) {
        this.fixtureType = fixtureType;
    }

}
