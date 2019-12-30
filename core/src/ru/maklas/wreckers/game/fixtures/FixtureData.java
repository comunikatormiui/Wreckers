package ru.maklas.wreckers.game.fixtures;

import ru.maklas.wreckers.game.FixtureType;

public class FixtureData {

	private static int idCounter = 0;
	private FixtureType fixtureType;
	private int id;

	public FixtureData(FixtureType fixtureType) {
		this.fixtureType = fixtureType;
		this.id = ++idCounter;
	}

	public FixtureType getFixtureType() {
		return fixtureType;
	}

	public void setFixtureType(FixtureType fixtureType) {
		this.fixtureType = fixtureType;
	}

	/** Unique id of this fixture **/
	public int getId() {
		return id;
	}
}
