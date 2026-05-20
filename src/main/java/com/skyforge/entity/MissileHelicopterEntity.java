package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.MissileHelicopterAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * Helicóptero cazabombardero con 4 pods de misiles y 2 MG.
 *
 * Torretas:
 *  turret_0  FREE_TRACKING  MG  cúpula superior izq.
 *  turret_1  FREE_TRACKING  MG  cúpula superior der.
 *  turret_2  LIMITED_TURN   AP_SHELL  pod izq. delantero
 *  turret_3  LIMITED_TURN   AP_SHELL  pod izq. trasero
 *  turret_4  LIMITED_TURN   AP_SHELL  pod der. delantero
 *  turret_5  LIMITED_TURN   AP_SHELL  pod der. trasero
 *
 * Pivots geo → Vec3 (x mismo signo, z negado):
 *  turret_2: [-36,24,-32] → Vec3(-2.25, 1.5,  2.0)
 *  turret_3: [-36,24, 32] → Vec3(-2.25, 1.5, -2.0)
 *  turret_4: [ 36,24,-32] → Vec3( 2.25, 1.5,  2.0)
 *  turret_5: [ 36,24, 32] → Vec3( 2.25, 1.5, -2.0)
 */
public class MissileHelicopterEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR =
            RawAnimation.begin().thenLoop("animation.missile_helicopter.rotor_spin");

    private static final FlightConfig MISSILE_HELI_FLIGHT = FlightConfig.builder()
            .maxSpeed(0.30f)
            .acceleration(0.025f)
            .turnRate(3.5f)
            .drag(0.982f)
            .canRotateInPlace(true)
            .build();

    public MissileHelicopterEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        addTurret(0, AimProfile.HELICOPTER,                                  CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(-0.75, 3.5, 2.5));
        addTurret(1, AimProfile.HELICOPTER,                                  CBCAmmoType.MACHINE_GUN_BULLET, new Vec3( 0.75, 3.5, 2.5));
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL,        new Vec3(-2.25, 1.5,  2.0));
        addTurret(3, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL,        new Vec3(-2.25, 1.5, -2.0));
        addTurret(4, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL,        new Vec3( 2.25, 1.5,  2.0));
        addTurret(5, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL,        new Vec3( 2.25, 1.5, -2.0));
        turretInit();

        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new MissileHelicopterCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, MISSILE_HELI_FLIGHT);
        this.attackController = new MissileHelicopterAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        r.add(new AnimationController<>(this, "rotor", 0, state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    60.0)
                .add(Attributes.FLYING_SPEED,   0.25)
                .add(Attributes.MOVEMENT_SPEED, 0.20)
                .add(Attributes.FOLLOW_RANGE,   80.0);
    }

    // ── Órbita más alejada para tiro de misiles ───────────────────────────────

    private static class MissileHelicopterCombatBehavior extends HelicopterCombatBehavior {
        MissileHelicopterCombatBehavior(MissileHelicopterEntity entity) {
            super(entity);
            this.orbitRadius     = 55;
            this.preferredHeight = 20;
            this.orbitSpeed      = 0.006;
        }
    }
}
