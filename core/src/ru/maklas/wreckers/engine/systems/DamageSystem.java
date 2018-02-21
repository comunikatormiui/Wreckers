package ru.maklas.wreckers.engine.systems;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.Subscription;
import ru.maklas.mengine.utils.Signal;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.client.entities.EntityNumber;
import ru.maklas.wreckers.engine.Mappers;
import ru.maklas.wreckers.engine.components.HealthComponent;
import ru.maklas.wreckers.engine.components.WeaponComponent;
import ru.maklas.wreckers.engine.components.WreckerComponent;
import ru.maklas.wreckers.engine.events.DamageEvent;
import ru.maklas.wreckers.engine.events.DeathEvent;
import ru.maklas.wreckers.engine.events.Event;
import ru.maklas.wreckers.engine.events.requests.WeaponWreckerHitEvent;
import ru.maklas.wreckers.libs.Utils;

public class DamageSystem extends EntitySystem {

    @Override
    public void onAddedToEngine(final Engine engine) {
        subscribe(new Subscription<WeaponWreckerHitEvent>(WeaponWreckerHitEvent.class) {
            @Override
            public void receive(Signal<WeaponWreckerHitEvent> signal, WeaponWreckerHitEvent e) {
                Entity weapon = e.getWeapon();
                WreckerComponent wreckC = e.getTargetWrecker().get(Mappers.wreckerM);
                WeaponComponent weapC = weapon.get(Mappers.weaponM);
                HealthComponent hc = e.getTargetWrecker().get(Mappers.healthM);
                if (weapC == null || wreckC == null || hc == null){
                    System.err.println("Wrecker or Weapon doesn't have stats to do damage");
                    return;
                }

                float trueDullDamage = e.getImpulse()  * e.getDullness()  * weapC.dullDamage;
                float trueSliceDamage = e.getImpulse() * e.getSharpness() * weapC.sliceDamage;

                float dullArmor  = ((100) / (100 + wreckC.dullArmor));
                float sliceArmor = ((100) / (100 + wreckC.sliceArmor));

                float dullDamage = trueDullDamage * dullArmor;
                float sliceDamage = trueSliceDamage * sliceArmor;

                float totalDamage = dullDamage + sliceDamage; // конечный ƒамаг
                float additionalImpulse = e.getWreckerBody().getMass() * (e.getImpulse() * ((weapC.hitImpulse - wreckC.stability) / 100) ); // дополнительное отбрасывание. может быть меньше нул€
                float stunChance = (e.getImpulse() * e.getDullness()) //мощность тупого удара
                        * (weapC.stunAbility - wreckC.stunResist); // скилл на резист. ћожет оказатьс€ отрицательным значением
                System.out.println("DULL HIT FORCE FOR STUN CALC: " + e.getImpulse() * e.getDullness());

                Vector2 box2dPos = Utils.vec1.set(e.getPoint()).scl(1 / GameAssets.box2dScale);
                Vector2 box2dImpulse = Utils.vec2.set(e.getNormal()).scl(additionalImpulse);
                e.getWreckerBody().applyForce(box2dImpulse, box2dPos, true);
                applyDamageAndDispatch(e.getTargetWrecker(), hc, totalDamage, e);

                engine.add(new EntityNumber((int) totalDamage, 2, e.getPoint().x, e.getPoint().y));
            }
        });
    }


    private void applyDamageAndDispatch(Entity e, HealthComponent hc, float damage, Event hitEvent){
        hc.health -= damage;
        getEngine().dispatch(new DamageEvent(e, damage, hitEvent));
        if (hc.health < 0){
            hc.health = 0;
            hc.dead = true;
            getEngine().dispatchLater(new DeathEvent(e, hitEvent));
        }
    }

}
