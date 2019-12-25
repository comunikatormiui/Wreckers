package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.game.entities.EntityArrow;
import ru.maklas.wreckers.game.entities.EntityNumber;
import ru.maklas.wreckers.game.entities.EntityString;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.components.WreckerComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.Event;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;
import ru.maklas.wreckers.engine.others.StunEffect;
import ru.maklas.wreckers.libs.Utils;
import ru.maklas.wreckers.network.events.NetHitEvent;

import java.util.Random;

import static ru.maklas.wreckers.assets.GameAssets.leagueFormula;

/**
 * <p>
 *     Система подписывается на HitEvent. Работает с HealthComponent. Обрабатывает момент удара,
 *     рассчитывает урон защиту, принимает во внимание эффекты и генерирует DamageEvent, DeathEvent.
 * </p>
 * <p>
 *     Вызывает эффекты возможные при ударе. Такие как стан, дополнительное отталкивание, застревание оружия в теле.
 * </p>
 */
public class HostDamageSystem extends EntitySystem {

    private final Vector2 vec1 = new Vector2();
    private final Vector2 vec2 = new Vector2();
    private final Random rand = new Random();
    private final GameModel model;


    public HostDamageSystem(GameModel model) {
        this.model = model;
    }

    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(WeaponWreckerHitEvent.class, e -> {
                model.updateOnThisFrame();
                Entity weapon = e.getWeapon();
                WreckerComponent wreckC = e.getTargetWrecker().get(Mappers.wreckerM);
                WeaponComponent weapC = weapon.get(Mappers.weaponM);
                HealthComponent hc = e.getTargetWrecker().get(Mappers.healthM);
                long currentTime = System.currentTimeMillis();
                final long timeBeforeNextDamage = (long) ((10 * 1000) /60f);
                if (weapC == null || wreckC == null || hc == null){
                    System.err.println("Wrecker or Weapon doesn't have stats to do damage");
                    return;
                } else
                if (currentTime - hc.lastDamageDone < timeBeforeNextDamage){ // имунитет к ударам на некоторое время
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
                boolean doStun = rand.nextFloat() < stunChance;
                float stunDuration = 0;
                if (doStun){
                    stunDuration = (weapC.stunAbility / 20) //Максимальный стан без резиста == 5 секунд
                            * leagueFormula(wreckC.stunResist); //
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
                //    engine.executeAfterUpdate(new Runnable() {
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
                //);
                //}
                applyDamageAndDispatch(e.getTargetWrecker(), hc, totalDamage, e, (doStun ? stunDuration : -1));  //Применяем урон


                //*********//
                //* ТЕСТЫ *//
                //*********//

                engine.add(new EntityNumber((int) totalDamage, 2, e.getPoint().x, e.getPoint().y));
                if (doStun){
                    engine.add(new EntityString("STUN! " + Utils.floatFormatted(stunDuration), 2, e.getPoint().x, e.getPoint().y + 50, Color.RED));
                    e.getTargetWrecker().get(Mappers.effectM).add(new StunEffect(stunDuration));
                }
                engine.add(new EntityString(
                        (int)(e.getDullness() * 100) + " / " +
                                (int) (e.getSliceness() * 100) + " / " +
                                (int) (e.getSharpness() * 100), 2, e.getPoint().x + (rand.nextFloat() * 50 - 25), e.getPoint().y + 25, Color.PINK));
                engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getCollisionVelocity()).nor().scl(75).add(e.getPoint()), 1, Color.ORANGE));
                engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getNormal()).scl(75).add(e.getPoint()), 1, Color.BROWN));
                if ( e.getSharpness() > 0.1f) engine.add(new EntityArrow(e.getPoint(), new Vector2(e.getPiercingDirection()).scl(75).add(e.getPoint()), 1, Color.CYAN));
            });
    }

    @Override
    public void update(float dt) {

    }

    private void applyDamageAndDispatch(Entity e, HealthComponent hc, float damage, Event hitEvent, float stunDuration){
        hc.health -= damage;
        hc.lastDamageDone = System.currentTimeMillis();
        getEngine().dispatch(new DamageEvent(e, damage, hitEvent));
        if (hc.health < 0){
            hc.health = 0;
            hc.dead = true;
            getEngine().dispatchLater(new DeathEvent(e, hitEvent));
        }

        if (hitEvent instanceof WeaponWreckerHitEvent){
            WeaponWreckerHitEvent wwh = (WeaponWreckerHitEvent) hitEvent;
            model.getSocket().send(new NetHitEvent(e.id, wwh.getWeapon().id, wwh.getPoint().x, wwh.getPoint().y, damage, hc.health, hc.dead, wwh.getSliceness(), wwh.getDullness(), wwh.getSharpness(), stunDuration));
        }
    }

}
