package ru.maklas.wreckers.engine.movemnet;

import ru.maklas.mengine.Component;

public class AntiGravComponent implements Component{

	public boolean antiGravEnabled = true;
	public boolean randomMovementEnabled = false;
	public float mass;

	public float maxX;
	public float maxY;
	public float dX;
	public float dY;
	public float changeSpeed;
	public boolean directionUp = true;
	public boolean directionRight = true;


	public AntiGravComponent(float mass, float maxX, float maxY, float changeSpeed) {
		this.mass = mass;
		this.maxX = maxX;
		this.maxY = maxY;
		this.changeSpeed = changeSpeed;
	}



}
