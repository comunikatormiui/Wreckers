package ru.maklas.wreckers.assets;

public class Masks {

    private static final int player = 0x1;
    private static final int zombie = 0x2;
    private static final int playerBullet = 0x4;
    private static final int zombieBullet = 0x8;
    private static final int friendlyObstacle = 0x10;
    private static final int obstacle = 0x20;

    private static final int maskPlayer = zombie | zombieBullet | obstacle;
    private static final int maskZombie = player | playerBullet | friendlyObstacle | obstacle;
    private static final int maskPlayerBullet = zombie | zombieBullet | obstacle;
    private static final int maskZombieBullet = player | friendlyObstacle | obstacle;
    private static final int maskObstacle = zombie | player | playerBullet | zombieBullet;
    private static final int maskFriendlyObstacle = zombie | zombieBullet;


    public static final Masks PLAYER = new Masks(
            1,
            player,
            maskPlayer
    );

    public static final Masks ZOMBIE = new Masks(
            2,
            zombie,
            maskZombie
    );

    public static final Masks PLAYER_BULLET = new Masks(
            3,
            playerBullet,
            maskPlayerBullet
    );

    public static final Masks ZOMBIE_BULLET = new Masks(
            4,
            zombieBullet,
            maskZombieBullet
    );

    public static final Masks FRIENDLY_OBSTACLE = new Masks(
            5,
            friendlyObstacle,
            maskFriendlyObstacle
    );

    public static final Masks OBSTACLE = new Masks(
            6,
            obstacle,
            maskObstacle
    );

    public static Masks fromType(int type){
        return values[type];
    }

    public static final Masks[] values = new Masks[]{
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

    public Masks(int type, int category, int mask) {
        this.type = type;
        this.category = (short) category;
        this.mask = (short) mask;
    }


}
