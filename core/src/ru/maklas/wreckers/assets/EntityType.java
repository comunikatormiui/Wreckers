package ru.maklas.wreckers.assets;

public class EntityType {

    private static final int player = 0x1;
    private static final int zombie = 0x2;
    private static final int playerBullet = 0x4;
    private static final int zombieBullet = 0x8;
    private static final int friendlyObstacle = 0x10;
    private static final int obstacle = 0x20;
    private static final int playerPickUp = 0x40;
    private static final int weaponPickUp = 0x80;

    private static final int maskPlayer = zombie | zombieBullet | obstacle;
    private static final int maskZombie = player | playerBullet | friendlyObstacle | obstacle;
    private static final int maskPlayerBullet = zombie | zombieBullet | obstacle;
    private static final int maskZombieBullet = player | friendlyObstacle | obstacle;
    private static final int maskObstacle = zombie | player | playerBullet | zombieBullet;
    private static final int maskFriendlyObstacle = zombie | zombieBullet;
    private static final int maskPlayerPickUp = weaponPickUp;
    private static final int maskWeaponPickUp = playerPickUp;


    public static final EntityType PLAYER = new EntityType(
            1,
            player,
            maskPlayer
    );

    public static final EntityType ZOMBIE = new EntityType(
            2,
            zombie,
            maskZombie
    );

    public static final EntityType PLAYER_BULLET = new EntityType(
            3,
            playerBullet,
            maskPlayerBullet
    );

    public static final EntityType ZOMBIE_BULLET = new EntityType(
            4,
            zombieBullet,
            maskZombieBullet
    );

    public static final EntityType FRIENDLY_OBSTACLE = new EntityType(
            5,
            friendlyObstacle,
            maskFriendlyObstacle
    );

    public static final EntityType OBSTACLE = new EntityType(
            6,
            obstacle,
            maskObstacle
    );

    public static final EntityType PLAYER_PICKUP = new EntityType(
            7,
            playerPickUp,
            maskPlayerPickUp
    );

    public static final EntityType WEAPON_PICKUP = new EntityType(
            8,
            weaponPickUp,
            maskWeaponPickUp
    );

    public static EntityType fromType(int type){
        return values[type];
    }

    public static final EntityType[] values = new EntityType[]{
            PLAYER,
            ZOMBIE,
            PLAYER_BULLET,
            ZOMBIE_BULLET,
            FRIENDLY_OBSTACLE,
            OBSTACLE
    };


    public final int type;
    public final short category;
    public final short mask;

    public EntityType(int type, int category, int mask) {
        this.type = type;
        this.category = (short) category;
        this.mask = (short) mask;
    }


}
