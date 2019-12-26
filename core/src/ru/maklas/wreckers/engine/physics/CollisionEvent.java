package ru.maklas.wreckers.engine.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.engine.other.Event;

/**
 * Ивент о коллизии. Зовётся после world.step(), так что можно спокойной взаимодействовать с физикой.
 * Используем mapMutate() для удобного определения типа Entity
 */
public class CollisionEvent implements Event {

    private Entity a;
    private Entity b;
    private Fixture fixA;
    private Fixture fixB;
    private final Vector2 point = new Vector2();
    private final Vector2 normal = new Vector2();

    public CollisionEvent() {

    }

    public CollisionEvent(Entity a, Entity b, Fixture fixA, Fixture fixB, Vector2 point, Vector2 normal) {
        this.a = a;
        this.b = b;
        this.fixA = fixA;
        this.fixB = fixB;
        this.point.set(point);
        this.normal.set(normal);
    }

    public CollisionEvent init(Entity a, Entity b, Fixture fixA, Fixture fixB, Vector2 point, Vector2 normal) {
        this.a = a;
        this.b = b;
        this.fixA = fixA;
        this.fixB = fixB;
        this.point.set(point);
        this.normal.set(normal);
        return this;
    }

    public Entity getA() {
        return a;
    }

    public Entity getB() {
        return b;
    }

    public Fixture getFixA() {
        return fixA;
    }

    public Fixture getFixB() {
        return fixB;
    }

    public Vector2 getPoint() {
        return point;
    }

    public Vector2 getNormal() {
        return normal;
    }

    /**
     * Меняет A и B местами, с учётом вектора нормали и возвращает. <b>Внимание! Мутирует данный объект!</b>
     */
    public CollisionEvent reverse(){
        return init(b, a, fixB, fixA, point, new Vector2(normal.x * -1, normal.y * -1));
    }

    /**
     * <p>
     *  <li>Если typeA и typeB соответствуют A и B, ничего не происходит и возвращается этот объект.</li>
     *  <li>Если typeA и typeB соответствуют B и A, возвращатеся CollisionEvent с перевёрнутыми A и B</li>
     *  <li>Если typeA и typeB не соответствуют A и B, возвращатеся <b>null</b></li>
     * </p>
     */
    public CollisionEvent map(int typeA, int typeB){
        if (this.a.type == typeA && this.b.type == typeB){
            return this;
        } else if (this.b.type == typeA && this.a.type == typeB){
            return new CollisionEvent(b, a, fixB, fixA, point, new Vector2(normal.x * -1, normal.y * -1));
        } else return null;
    }

    /**
     * <p>
     *     <b>Внимание! Может мутировать данный объект!</b>
     *  <li>Если typeA и typeB соответствуют A и B, ничего не происходит и возвращается этот объект.</li>
     *  <li>Если typeA и typeB соответствуют B и A, возвращатеся этот объект, но его B и A меняются местами</li>
     *  <li>Если typeA и typeB не соответствуют A и B, возвращатеся <b>null</b></li>
     * </p>
     */
    public CollisionEvent mapMutate(int typeA, int typeB){
        if (this.a.type == typeA && this.b.type == typeB){
            return this;
        } else if (this.b.type == typeA && this.a.type == typeB){
            return reverse();
        } else return null;
    }

    /**
     * Ведёт себя как и {@link #mapMutate(int, int)}, но проверяет только тип одного из Entity.
     */
    public CollisionEvent mapMutate(int typeA){
        if (this.a.type == typeA){
            return this;
        } else if (this.b.type == typeA){
            return reverse();
        } else return null;
    }

}
