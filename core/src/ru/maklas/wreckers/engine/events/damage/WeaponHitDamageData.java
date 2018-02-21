package ru.maklas.wreckers.engine.events.damage;

import com.badlogic.gdx.math.Vector2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.events.DamageType;

public class WeaponHitDamageData extends DamageData {

    Entity weapon;
    @Nullable Entity weaponOwner;
    Vector2 point;
    Vector2 normal;

    public WeaponHitDamageData(@NotNull Entity target, float damage, @NotNull Entity weapon, @Nullable Entity weaponOwner, @NotNull Vector2 point, @NotNull Vector2 normal) {
        super(DamageType.HIT, target, weaponOwner, damage);
        this.weapon = weapon;
        this.weaponOwner = weaponOwner;
        this.point = point;
        this.normal = normal;
    }

    public Entity getWeapon() {
        return weapon;
    }

    public Vector2 getPoint() {
        return point;
    }

    public Vector2 getNormal() {
        return normal;
    }

    @Nullable
    public Entity getWeaponOwner(){
        return weaponOwner;
    }
}
