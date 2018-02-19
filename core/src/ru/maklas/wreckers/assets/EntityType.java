package ru.maklas.wreckers.assets;

public class EntityType {

    private static final int player = 0x1;
    private static final int opponent = 0x2;
    private static final int playerWeapon = 0x4;
    private static final int opponentWeapon = 0x8;
    private static final int neutralWeapon = 0x10;

    private static final int obstacle = 0x20;

    private static final int wielderPickUp = 0x40;
    private static final int weaponPickUp = 0x80;

    private static final int maskPlayer         = opponent | opponentWeapon | obstacle | neutralWeapon;
    private static final int maskPlayerWeapon   = opponent | opponentWeapon | obstacle | neutralWeapon;
    private static final int maskOpponent       = player | playerWeapon | obstacle | neutralWeapon;
    private static final int maskOpponentWeapon = player | playerWeapon | obstacle | neutralWeapon;
    private static final int maskNeutralWeapon  = player | opponent | playerWeapon | opponentWeapon | neutralWeapon | obstacle;

    private static final int maskObstacle = opponent | player | playerWeapon | opponentWeapon | neutralWeapon;

    private static final int maskWielderPickUp = weaponPickUp;
    private static final int maskWeaponPickUp = wielderPickUp;


    public static final EntityType PLAYER = new EntityType(
            1,
            player,
            maskPlayer
    );

    public static final EntityType OPPONENT = new EntityType(
            2,
            opponent,
            maskOpponent
    );

    public static final EntityType PLAYER_WEAPON = new EntityType(
            3,
            playerWeapon,
            maskPlayerWeapon
    );

    public static final EntityType OPPONENT_WEAPON = new EntityType(
            4,
            opponentWeapon,
            maskOpponentWeapon
    );

    public static final EntityType NEUTRAL_WEAPON = new EntityType(
            5,
            neutralWeapon,
            maskNeutralWeapon
    );

    public static final EntityType OBSTACLE = new EntityType(
            6,
            obstacle,
            maskObstacle
    );

    public static final EntityType PLAYER_PICKUP = new EntityType(
            7,
            wielderPickUp,
            maskWielderPickUp
    );

    public static final EntityType WEAPON_PICKUP = new EntityType(
            8,
            weaponPickUp,
            maskWeaponPickUp
    );

    public static final EntityType[] values = new EntityType[]{
            null,
            PLAYER,
            OPPONENT,
            PLAYER_WEAPON,
            OPPONENT_WEAPON,
            NEUTRAL_WEAPON,
            OBSTACLE,
            PLAYER_PICKUP,
            WEAPON_PICKUP
    };


    public final int type;
    public final short category;
    public final short mask;

    private EntityType(int type, int category, int mask) {
        this.type = type;
        this.category = (short) category;
        this.mask = (short) mask;
    }

    @Override
    public String toString() {
        return "EntityType{" +
                "type=" + type +
                ", category=" + category +
                ", mask=" + mask +
                '}';
    }


    public static EntityType fromType(int type){
        return values[type];
    }

    public static boolean isWeapon(EntityType type){
        return type == PLAYER_WEAPON | type == OPPONENT_WEAPON | type == NEUTRAL_WEAPON;
    }

    public static boolean isPlayerOrOpponent(EntityType type){
        return type == PLAYER | type == OPPONENT;
    }


    public static EntityType weaponTypeFor(EntityType eType) {
        if (eType == PLAYER){
            return PLAYER_WEAPON;
        } else if (eType == OPPONENT){
            return OPPONENT_WEAPON;
        }
        throw new RuntimeException("No weapon for " + eType);
    }
}
