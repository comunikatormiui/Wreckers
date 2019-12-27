package ru.maklas.wreckers.game;

public enum FixtureType {

	/** Body of a wrecker **/
	WRECKER_BODY,
	/** Default damaging part of the Weapon**/
	WEAPON_DAMAGE,
	/** Weapon's part that does no damage for security of wielding**/
	WEAPON_NO_DAMAGE,
	/** Platforms, Environment **/
	OBSTACLE,
	/** Sensor around wrecker that allows for weapon pick-up **/
	GRABBER_SENSOR,
	/** Sensor around weapon that allows it to be picked up **/
	PICKUP_SENSOR

}
