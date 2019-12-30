package ru.maklas.wreckers.engine.health;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.SubscriptionSystem;
import ru.maklas.wreckers.engine.B;
import ru.maklas.wreckers.engine.M;
import ru.maklas.wreckers.engine.other.Event;
import ru.maklas.wreckers.engine.physics.WeaponWreckerHitEvent;
import ru.maklas.wreckers.engine.status_effects.StunEffect;
import ru.maklas.wreckers.engine.weapon.WeaponComponent;
import ru.maklas.wreckers.engine.wrecker.WreckerComponent;
import ru.maklas.wreckers.game.entities.EntityNumber;
import ru.maklas.wreckers.game.entities.EntityString;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.StringUtils;

import java.util.Random;

import static ru.maklas.wreckers.statics.Game.leagueFormula;

/** Consumes WeaponWreckerHitEvent and applies damage **/
public class DamageSystem extends SubscriptionSystem {

	private final Vector2 tempVec = new Vector2();
	private final Random rand = new Random();

	@Override
	public void onAddedToEngine(final Engine engine) {
		subscribe(WeaponWreckerHitEvent.class, this::onWeaponWreckerHitEvent);
	}

	private void onWeaponWreckerHitEvent(WeaponWreckerHitEvent e) {
		engine.getBundler().set(B.updateThisFrame, true);
		Entity weapon = e.getWeapon();
		WreckerComponent wreckC = e.getTargetWrecker().get(M.wrecker);
		WeaponComponent weapC = weapon.get(M.weapon);
		HealthComponent hc = e.getTargetWrecker().get(M.health);
		long currentTime = System.currentTimeMillis();
		final long timeBeforeNextDamage = (long) (10 * (1000 /60.0));
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
		float trueDullDamage   = impulse	* e.getDullness()  * weapC.dullDamage   * weapC.dullAdjustment   * impulseAdjustment;
		float trueSliceDamage  = velAtPoint * e.getSliceness() * weapC.sliceDamage  * weapC.sliceAdjustment  * velocityAdjustment;
		float truePierceDamage = impulse    * e.getSharpness() * weapC.pierceDamage * weapC.pierceAdjustment * impulseAdjustment;

		//Урон с армором
		float dullDamage = trueDullDamage   * leagueFormula(wreckC.dullArmor);
		float sliceDamage = trueSliceDamage * leagueFormula(wreckC.sliceArmor);
		float pierceDamage = truePierceDamage * leagueFormula(wreckC.pierceArmor);

		float totalDamage = dullDamage + sliceDamage + pierceDamage; // конечный Дамаг
		if (totalDamage > 5) {
			Log.debug("Damage applied. {dull=" + StringUtils.ff(dullDamage) + ", slice=" + StringUtils.ff(sliceDamage) + ", pierce=" + StringUtils.ff(pierceDamage) + "}");
		}

		//************************//
		//* РАССЧЁТ ДОП ИМПУЛЬСА *//
		//************************//

		float additionalImpulse =
				e.getDullness() > e.getSharpness() ? // если удар был острым, а не тупым, то не отбрасываем дополонительно.
						e.getWreckerFix().getBody().getMass() * (impulse * ((weapC.hitImpulse * leagueFormula(wreckC.stability)) / 100)) // дополнительное отбрасывание. может быть меньше нуля
						: 0;


		//*****************//
		//* РАССЧЁТ СТАНА *//
		//*****************//

		float dullHitForce = ((impulse * e.getDullness()) / 500);
		dullHitForce = dullHitForce > 1 ? 1 : dullHitForce;   // мощность тупого удара. 0..1
		float stunChance = dullHitForce * (weapC.stunAbility / 100f) //полностью зависит от статы оружия. 1, только если stunAbility == 100
				* leagueFormula(wreckC.stunResist); // Добавляем резисты.
		boolean doStun = rand.nextFloat() < stunChance;
		float stunDuration = 0;
		if (doStun){
			stunDuration = (weapC.stunAbility / 20) //Максимальный стан без резиста == 5 секунд
					* leagueFormula(wreckC.stunResist); //
		}


		//**************//
		//* ПРИМЕНЕНИЕ *//
		//**************//

		if (additionalImpulse > 1) {
			Vector2 box2dImpulse = tempVec.set(e.getCollisionVelocity()).nor().scl(additionalImpulse);
			e.getWreckerFix().getBody().applyForceToCenter(box2dImpulse, true); //Применяем доп импульс.
		}

		applyDamageAndDispatch(e.getTargetWrecker(), hc, totalDamage, e, (doStun ? stunDuration : -1));  //Применяем урон


		//*********//
		//* ТЕСТЫ *//
		//*********//

		engine.add(new EntityNumber((int) totalDamage, 2, e.getPoint().x, e.getPoint().y, Color.RED));
		if (doStun){
			e.getTargetWrecker().get(M.effect).add(new StunEffect(stunDuration));
			engine.add(new EntityString("STUN! " + StringUtils.ff(stunDuration), 2, e.getPoint().x + 30, e.getPoint().y + 75, Color.RED));
		}
		engine.addAll(EntityString.multiColor(e.getPoint().x + (rand.nextFloat() * 50 - 25), e.getPoint().y + 125, 2,
				new EntityString.Section(String.valueOf((int)(e.getDullness() * 100)), Color.ORANGE),
				new EntityString.Section(" / ", Color.WHITE),
				new EntityString.Section(String.valueOf((int)(e.getSliceness() * 100)), Color.VIOLET),
				new EntityString.Section(" / ", Color.WHITE),
				new EntityString.Section(String.valueOf((int)(e.getSharpness() * 100)), Color.SKY)
		));
	}


	protected void applyDamageAndDispatch(Entity e, HealthComponent hc, float damage, Event hitEvent, float stunDuration){
		hc.health -= damage;
		hc.lastDamageDone = System.currentTimeMillis();
		getEngine().dispatch(new DamageEvent(e, damage, hitEvent));
		if (hc.health < 0){
			hc.health = 0;
			hc.alive = false;
			getEngine().dispatchLater(new DeathEvent(e, hitEvent));
		}
	}

}
