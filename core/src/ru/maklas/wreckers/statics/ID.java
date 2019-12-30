package ru.maklas.wreckers.statics;

import ru.maklas.libs.Counter;

public class ID {

	public static final int camera = 2;
	//singleplayer
	public static final int soloPlayer = 100;
	public static final int soloOpponent = 200;
	public static int floor = 1;
	public static int multiplayerHost = 1000;
	public static int multiplayerJoin = 2000;


	public static Counter counterForEnvironment(){
		return new Counter(3_000_000, 4_000_000);
	}
}
