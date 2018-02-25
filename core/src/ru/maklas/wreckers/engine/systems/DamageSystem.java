package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.entities.EntityArrow;
import ru.maklas.wreckers.client.entities.EntityNumber;
import ru.maklas.wreckers.client.entities.EntityString;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.components.WreckerComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.Event;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;

import java.util.Random;

public class DamageSystem extends EntitySystem {

    private final Vector2 vec1 = new Vector2();
    private final Vector2 vec2 = new Vector2();
    private final Random random = new Random();


    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(new Subscription<WeaponWreckerHitEvent>(WeaponWreckerHitEvent.class) {
            @Override
            public void receive(Signal<WeaponWreckerHitEvent> signal, final WeaponWreckerHitEvent e) {
                Entity weapon = e.getWeapon();
                WreckerComponent wreckC = e.getTargetWrecker().get(Mappers.wreckerM);
                WeaponComponent weapC = weapon.get(Mappers.weaponM);
                HealthComponent hc = e.getTargetWrecker().get(Mappers.healthM);
                long currentTime = System.currentTimeMillis();
                final long timeBeforeNextDamage = (long) ((10 * 1000) /60f);
                if (weapC == null || wreckC == null || hc == null || (currentTime - hc.lastDamageDone < timeBeforeNextDamage)){
                    //TODO System.err.println("Wrecker or Weapon doesn't have stats to do damage");
                    return;
                }
                float impulse = e.getImpulse();
                float velAtPoint = e.getCollisionVelocity().len();


                //*****************//
                //* РАССЧЁТ УРОНА *//
                //*****************//

                //Настройки
                final float impulseAdjustment = e.getWeaponOwner() == null  ? 1/1000f : 1/300f; // Оружие без владельца наносит в разы меньше урона
                final float velocityAdjustment = e.getWeaponOwner() == null ? 0.04f : 0.133f;

                //Чистый урон
                float trueDullDamage  = impulse    * e.getDullness()  * weapC.dullDamage   * weapC.dullAdjustment   * impulseAdjustment;
                float trueSliceDamage = velAtPoint * e.getSliceness() * weapC.sliceDamage  * weapC.sliceAdjustment  * velocityAdjustment;
                float truePierceDamage = impulse   * e.getSharpness() * weapC.pierceDamage * weapC.pierceAdjustment * impulseAdjustment;

                //Урон с армором
                float dullDamage = trueDullDamage   * leagueFormula(wreckC.dullArmor);
                float sliceDamage = trueSliceDamage * leagueFormula(wreckC.sliceArmor);
                float pierceDamage = truePierceDamage * leagueFormula(wreckC.pierceArmor);

                float totalDamage = dullDamage + sliceDamage + pierceDamage; // конечный Дамаг

                //************************//
                //* РАССЧЁТ ДОП ИМПУЛЬСА *//
                //************************//

                float additionalImpulse =
                        e.getSharpness() > e.getDullness() ? // если удар был острым, а не тупым, то не отбрасываем дополонительно.
                        0 :
                        e.getWreckerBody().getMass() * (impulse * ((weapC.hitImpulse * leagueFormula(wreckC.stability)) / 100) ); // дополнительное отбрасывание. может быть меньше нуля


                //*****************//
                //* РАССЧЁТ СТАНА *//
                //*****************//

                float dullHitForce = ((impulse * e.getDullness()) / 500);
                dullHitForce = dullHitForce > 1 ? 1 : dullHitForce;   // мощность тупого удара. 0..1
                float stunChance = dullHitForce * (weapC.stunAbility / 100f) //полностью зависит от статы оружия. 1, только если stunAbility == 100
                        * leagueFormula(wreckC.stunResist); // Добавляем резисты.
                System.out.println("Stun chance: " + stunChance);
                boolean doStun = random.nextFloat() < stunChance;
                float stunDuration = 0;
                if (doStun){
                    stunDuration = weapC.stunAbility / 40f; //До 2.5 секунд стана.
                }

                //***********************//
                //* РАССЧЁТ ЗАСТРЕВАНИЯ *//
                //***********************//
                boolean stuck = e.getSharpness() > 0.7f && impulse > 200; //TODO


                //**************//
                //* ПРИМЕНЕНИЕ *//
                //**************//

                if (additionalImpulse > 1) {
                    Vector2 box2dPos = vec1.set(e.getPoint()).scl(1 / GameAssets.box2dScale);
                    Vector2 box2dImpulse = vec2.set(e.getCollisionVelocity()).nor().scl(-additionalImpulse);
                    e.getWreckerBody().applyForceToCenter(box2dImpulse, true); //Применяем доп импульс.
                }
                //if (stuck){ //TODO
                //    System.out.println("Stuck");
                //    engine.execureAfterUpdate(new Runnable() {
                //        @Override
                //        public void run() {
                //            WeldJointDef wjd = new WeldJointDef();
                //            wjd.bodyA = e.getWeaponBody();
                //            wjd.bodyB = e.getWreckerBody();
                //            wjd.collideConnected = false;
                //            wjd.localAnchorA.set(e.getWeaponStuckPoint());
                //            wjd.localAnchorB.set(e.getWreckerBody().getLocalPoint(new Vector2(e.getPoint()).scl( 1 / GameAssets.box2dScale)));
                //            World world = e.getWeaponBody().getWorld();
                //            world.createJoint(wjd);
                //            System.out.println("WJD: " + new Vector2(e.getPoint()).scl(1 / GameAssets.box2dScale));
                //            System.out.println("WJD: " + e.getWeaponBody().getWorldPoint(wjd.localAnchorA));
                //            System.out.println("WJD: " + e.getWreckerBody().getWorldPoint(wjd.localAnchorB));
                //        }
                //    });
                //}
                applyDamageAndDispatch(e.getTargetWrecker(), hc, totalDamage, e);  //Применяем урон


                //*********//
                //* ТЕСТЫ *//
                //*********//

                engine.add(new EntityNumber((int) totalDamage, 2, e.getPoint().x, e.getPoint().y));
                if (doStun){
                    engine.add(new EntityString("STUN!", 2, e.getPoint().x, e.getPoint().y + 50, Color.RED));
                }
                engine.add(new EntityString(
                        (int)(e.getDullness() * 100) + " / " +
                                (int) (e.getSliceness() * 100) + " / " +
                                (int) (e.getSharpness() * 100), 2, e.getPoint().x + (random.nextFloat() * 50 - 25), e.getPoint().y + 25, Color.PINK));
                engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getCollisionVelocity()).nor().scl(75).add(e.getPoint()), 1, Color.ORANGE));
                engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getNormal()).scl(75).add(e.getPoint()), 1, Color.BROWN));
                if ( e.getSharpness() > 0.1f) engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getPiercingDirection()).scl(75).add(e.getPoint()), 1, Color.CYAN));
            }
        });
    }


    private void applyDamageAndDispatch(Entity e, HealthComponent hc, float damage, Event hitEvent){
        hc.health -= damage;
        hc.lastDamageDone = System.currentTimeMillis();
        getEngine().dispatch(new DamageEvent(e, damage, hitEvent));
        if (hc.health < 0){
            hc.health = 0;
            hc.dead = true;
            getEngine().dispatchLater(new DeathEvent(e, hitEvent));
        }
    }

    /**
     * Возвращает процент проходимого урона после учёта резистов. Применяется так:
     * <p>уронСУчётомРезистов = чистыйУрон * leagueFormula(резист);</p>
     */
    private float leagueFormula(float resist){
        if (resist < -50){
            resist = -50;
        }
        return ((100) / (100 + resist));
    }

}
