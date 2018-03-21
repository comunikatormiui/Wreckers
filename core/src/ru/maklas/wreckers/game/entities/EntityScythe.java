package ru.maklas.wreckers.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ru.maklas.bodymaker.impl.save_beans.BodyPoly;
import ru.maklas.bodymaker.impl.save_beans.FixShape;
import ru.maklas.bodymaker.impl.save_beans.NamedPoint;
import ru.maklas.mengine.Entity;
import ru.maklas.wreckers.assets.EntityType;
import ru.maklas.wreckers.assets.GameAssets;
import ru.maklas.wreckers.assets.Images;
import ru.maklas.wreckers.client.GameModel;
import ru.maklas.wreckers.engine.components.*;
import ru.maklas.wreckers.engine.components.rendering.RenderComponent;
import ru.maklas.wreckers.engine.components.rendering.RenderUnit;
import ru.maklas.wreckers.game.fixtures.FixtureData;
import ru.maklas.wreckers.game.FixtureType;
import ru.maklas.wreckers.game.fixtures.WeaponPiercingFD;

import java.util.logging.FileHandler;

public class EntityScythe extends WeaponEntity implements AttachAction {


    Body body;
    World world;
    PickUpComponent pickUpC;
    final float scale = 0.65f;
    float handleX;
    float handleY;

    public EntityScythe(int id, float x, float y, int zOrder, GameModel model) {
        super(id, x, y, zOrder, model);
        this.world = model.getWorld();
        final EntityType eType = EntityType.NEUTRAL_WEAPON;

        BodyPoly bodyPoly = BodyPoly.fromJson(Gdx.files.internal("scythe.json").readString());

        NamedPoint mass_center = bodyPoly.getMassCenter();
        NamedPoint centerWannaBe = bodyPoly.findPoint("centerWannaBe");
        NamedPoint peak = bodyPoly.findPoint("peak");
        NamedPoint origin = bodyPoly.findPoint("Origin");

        RenderUnit unit = new RenderUnit(Images.scythe);
        unit.scaleX = unit.scaleY = scale;
        unit.pivotX = mass_center.x / unit.width;
        unit.pivotY = mass_center.y / unit.height;

        bodyPoly
                .mov(-mass_center.x, -mass_center.y) // теперь координаты (0, 0) совпадают с координатами центра массы
                .scale(scale/GameAssets.box2dScale); // Подгоняем размер.



        PolygonShape handle     = bodyPoly.findShape("handle").toPolygonShape();
        PolygonShape hammerSide = bodyPoly.findShape("hammerSide").toPolygonShape();
        PolygonShape blade      = bodyPoly.findShape("blade").toPolygonShape();
        PolygonShape blade2     = bodyPoly.findShape("blade2").toPolygonShape();
        PolygonShape edge       = bodyPoly.findShape("edge").toPolygonShape();


        FixtureDef fix = model.getFixturer().newFixture()
                .bounciness(0.1f)
                .mask(eType)
                .friction(1f)
                .shape(handle)
                .density(1)
                .build();

        FixtureDef fix2 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(hammerSide)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        FixtureDef fix3 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(blade)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        FixtureDef fix4 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(blade2)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        FixtureDef fix5 = model.getFixturer().newFixture()
                .mask(eType)
                .shape(edge)
                .friction(0.2f)
                .bounciness(0.1f)
                .density(1)
                .build();

        body = model.getBuilder().newBody()
                .pos(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .linearDamp(0.1f)
                .addFixture(fix, new FixtureData(FixtureType.WEAPON_NO_DAMAGE))
                .addFixture(fix2, new FixtureData(FixtureType.WEAPON_DAMAGE))
                .addFixture(fix3, new FixtureData(FixtureType.WEAPON_DAMAGE))
                .addFixture(fix4, new FixtureData(FixtureType.WEAPON_DAMAGE))
                .addFixture(fix5, new WeaponPiercingFD(0.5f, -2f, 217 * scale / GameAssets.box2dScale, 60 * scale / GameAssets.box2dScale))
                .angularDamp(0.1f)
                .build();

        System.out.println(id + ": Scythe mass " + body.getMass());

        add(new PhysicsComponent(body));
        add(new RenderComponent(unit));
        handleX = origin.x * GameAssets.box2dScale + 30 * scale;
        handleY = origin.y * GameAssets.box2dScale + 30 * scale;
        pickUpC = new PickUpComponent(model.getShaper().buildCircle(handleX, handleY, 35), this);
        add(pickUpC);
        add(new WeaponComponent(
                10,
                50,
                50,
                1,
                1,
                1,
                12,
                55,
                25));
    }

    @Override
    public JointDef attach(Entity owner, WSocket socket, Body ownerBody) {
        RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = ownerBody;
        rjd.bodyB = this.body;
        rjd.localAnchorA.set(socket.localX, socket.localY);
        rjd.localAnchorB.set(handleX, handleY).scl(1 / GameAssets.box2dScale);
        return rjd;
    }
}
