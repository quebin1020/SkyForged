package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.HelicopterAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
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

/**
 * Gunship — helicóptero de combate pesado.
 * 4 torretas: 2 MG laterales + 2 cañones HE frontales.
 * Órbita más grande y vuelo más estable que el DebugHelicopter.
 */
public class GunshipEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR =
            RawAnimation.begin().thenLoop("animation.gunship.rotor_spin");

    public GunshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas ──────────────────────────────────────────────────────────
        // turret_0: cañón frontal izq — MG
        // turret_1: cañón frontal der — MG
        // turret_2: lateral izq — AP
        // turret_3: lateral der — HE
        addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(-1.5, 2.0,  4.0));
        addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3( 1.5, 2.0,  4.0));
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHOT,   new Vec3(-3.0, 1.5,  0.0));
        addTurret(3, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.HE_SHELL,  new Vec3( 3.0, 1.5,  0.0));
        turretInit();

        // ── Sistemas de vuelo ─────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new GunshipCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, FlightConfig.GUNSHIP);
        this.attackController = new HelicopterAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "rotor", 0,
                state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    80.0)
                .add(Attributes.FLYING_SPEED,  0.35)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE,  96.0);
    }

    // ── Comportamiento orbital más amplio del gunship ─────────────────────────

    private static class GunshipCombatBehavior extends HelicopterCombatBehavior {
        GunshipCombatBehavior(GunshipEntity entity) {
            super(entity);
            this.orbitRadius     = 60;
            this.preferredHeight = 22;
            this.orbitSpeed      = 0.006;
        }
    }
}
