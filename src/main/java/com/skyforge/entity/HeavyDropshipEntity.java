package com.skyforge.entity;

import com.skyforge.ai.combat.AimProfile;
import com.skyforge.config.FlightConfig;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.HelicopterMovement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Dropship pesado: 2 torretas defensivas MG, deposita 6 vindicators + 3 witches.
 *
 * Turrets:
 *  turret_0  MG  lado izquierdo
 *  turret_1  MG  lado derecho
 */
public class HeavyDropshipEntity extends AbstractDropshipEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR =
            RawAnimation.begin().thenLoop("animation.heavy_dropship.rotor_spin");

    private static final FlightConfig HEAVY_DROPSHIP_FLIGHT = FlightConfig.builder()
            .maxSpeed(0.16f)
            .acceleration(0.012f)
            .turnRate(1.8f)
            .drag(0.986f)
            .canRotateInPlace(true)
            .build();

    public HeavyDropshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        // Two defensive MG turrets on flanks
        addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(-2.5, 4.0,  0.0));
        addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3( 2.5, 4.0,  0.0));
        turretInit();

        initDropshipSystems();
        this.movement = new HelicopterMovement(this, HEAVY_DROPSHIP_FLIGHT);
    }

    @Override
    protected List<EntityType<? extends Mob>> buildManifest() {
        return List.of(
                EntityType.VINDICATOR,
                EntityType.VINDICATOR,
                EntityType.WITCH,
                EntityType.VINDICATOR,
                EntityType.VINDICATOR,
                EntityType.WITCH,
                EntityType.VINDICATOR,
                EntityType.VINDICATOR,
                EntityType.WITCH
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        r.add(new AnimationController<>(this, "rotor", 0, state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override protected float getDeathExplosionRadius() { return 5.5f; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    90.0)
                .add(Attributes.FLYING_SPEED,   0.14)
                .add(Attributes.MOVEMENT_SPEED, 0.12)
                .add(Attributes.FOLLOW_RANGE,   70.0)
                .add(Attributes.ARMOR,           2.0);
    }
}
