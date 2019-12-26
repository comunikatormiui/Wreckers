package ru.maklas.wreckers.statics;

public class EntityType {


	public static final int CAMERA = 0;
	public static final int PLAYER = 1;
	public static final int OPPONENT = 2;
	public static final int PLAYER_WEAPON = 3;
	public static final int OPPONENT_WEAPON = 4;
	public static final int NEUTRAL_WEAPON = 5;
	public static final int OBSTACLE = 6;
	public static final int GRABBER = 7;
	public static final int WEAPON_PICKUP = 8;

	private static final EntityType[] map;

	static {

		//Masks

		int none = 				0b1;
		int player = 			0b10;
		int opponent = 			0b100;
		int playerWeapon = 		0b1000;
		int opponentWeapon =	0b10000;
		int neutralWeapon = 	0b100000;
		int obstacle = 			0b1000000;
		int grabber = 			0b10000000;
		int weaponPickUp = 		0b100000000;


		map = new EntityType[16];

		map[CAMERA] = 			new EntityType(none, none);
		map[PLAYER] = 			new EntityType(player, opponent | opponentWeapon | obstacle | neutralWeapon);
		map[OPPONENT] = 		new EntityType(opponent, player | playerWeapon | obstacle | neutralWeapon);
		map[PLAYER_WEAPON] = 	new EntityType(playerWeapon, opponent | opponentWeapon | obstacle | neutralWeapon);
		map[OPPONENT_WEAPON] = 	new EntityType(opponentWeapon, player | playerWeapon | obstacle | neutralWeapon);
		map[NEUTRAL_WEAPON] = 	new EntityType(neutralWeapon, player | opponent | playerWeapon | opponentWeapon | neutralWeapon | obstacle);
		map[OBSTACLE] = 		new EntityType(obstacle, opponent | player | playerWeapon | opponentWeapon | neutralWeapon);
		map[GRABBER] = 			new EntityType(grabber, weaponPickUp);
		map[WEAPON_PICKUP] = 	new EntityType(weaponPickUp, grabber);
	}


	public final short category;
	public final short mask;

	private EntityType(int category, int mask) {
		this.category = (short) category;
		this.mask = (short) mask;
	}

	@Override
	public String toString() {
		return "EntityType{" +
				", category=" + category +
				", mask=" + mask +
				'}';
	}

	public static EntityType of(int type){
		return map[type];
	}

	public static EntityType of(int code, EntityType def){
		if (code > 0 && code < map.length) {
			return map[code];
		}
		return def;
	}

	public static boolean isWeapon(int type){
		return type == PLAYER_WEAPON | type == OPPONENT_WEAPON | type == NEUTRAL_WEAPON;
	}

	public static boolean isPlayerOrOpponent(int type){
		return type == PLAYER | type == OPPONENT;
	}


	public static int weaponTypeFor(int eType) {
		if (eType == PLAYER){
			return PLAYER_WEAPON;
		} else if (eType == OPPONENT){
			return OPPONENT_WEAPON;
		}
		throw new RuntimeException("No weapon for " + eType);
	}
}
