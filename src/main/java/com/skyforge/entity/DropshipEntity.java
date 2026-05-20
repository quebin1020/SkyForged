package com.skyforge.entity;

import com.skyforge.config.FlightConfig;
import com.skyforge.movement.HelicopterMovement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Dropship ligero: deposita 4 pillagers + 2 vindicators.
 */
public class DropshipEntity extends AbstractDropshipEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR =
            RawAnimation.begin().thenLoop("animation.dropship.rotor_spin");

    private static final FlightConfig DROPSHIP_FLIGHT = FlightConfig.builder()
            .maxSpeed(0.22f)
            .acceleration(0.018f)
            .turnRate(2.8f)
            .drag(0.984f)
            .canRotateInPlace(true)
            .build();

    public DropshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        // Single defensive MG on top
        addTurret(0, com.skyforge.ai.combat.AimProfile.HELICOPTER,
                com.skyforge.integration.cbc.CBCAmmoType.MACHINE_GUN_BULLET,
                new net.minecraft.world.phys.Vec3(0, 4.5, 1.5));
        turretInit();

        initDropshipSystems();
        this.movement = new HelicopterMovement(this, DROPSHIP_FLIGHT);
    }

    @Override
    protected List<EntityType<? extends Mob>> buildManifest() {
        return List.of(
                EntityType.PILLAGER,
                EntityType.PILLAGER,
                EntityType.VINDICATOR,
                EntityType.PILLAGER,
                EntityType.PILLAGER,
                EntityType.VINDICATOR
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        r.add(new AnimationController<>(this, "rotor", 0, state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    50.0)
                .add(Attributes.FLYING_SPEED,   0.18)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE,   60.0);
    }
}
