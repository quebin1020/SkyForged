package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.HelicopterAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
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

public class DebugHelicopterEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR =
            RawAnimation.begin().thenLoop("animation.helicopter.rotor_spin");

    public DebugHelicopterEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas ──────────────────────────────────────────────────────────
        // turret_0: cañón frontal inferior (MG rápida)
        // turret_1: cañón trasero inferior (solid shot pesado)
        addTurret(0, AimProfile.HELICOPTER, CBCAmmoType.MACHINE_GUN_BULLET,
                new Vec3(0, 2.0, 3.5));
        addTurret(1, AimProfile.HELICOPTER, CBCAmmoType.SOLID_SHOT,
                new Vec3(0, 2.0, -0.5));
        turretInit();

        // ── Sistemas de vuelo ─────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new HelicopterCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, FlightConfig.HELICOPTER);
        this.attackController = new HelicopterAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "rotor", 0,
                state -> state.setAndContinue(ANIM_ROTOR)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        if (brain != null && tickCount % 5 == 0 && brain.getPatrolTarget() != null) {
            DebugRender.drawLine(level(), this.position(), brain.getPatrolTarget());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    20.0)
                .add(Attributes.FLYING_SPEED,   0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE,  64.0);
    }
}
