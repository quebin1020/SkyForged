package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.HelicopterAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.movement.HelicopterMovement;
import com.skyforge.targeting.TargetingSystem;
import com.skyforge.util.DebugRender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DebugHelicopterEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Nombre de las animaciones definidas en helicopter.animation.json
    private static final RawAnimation ANIM_ROTOR_SPIN =
            RawAnimation.begin().thenLoop("animation.helicopter.rotor_spin");

    public DebugHelicopterEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        initTurrets();
        for (AimController turret : turrets.values()) {
            turret.setMode(AimController.AimMode.FREE_TRACKING);
        }

        FlightConfig config = new FlightConfig(
                0.35f,
                0.03f,
                4f,
                0.02f,
                0.98f,
                true,
                false
        );

        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new HelicopterCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, config);
        this.attackController = new HelicopterAttackController(this);
    }

    // ── GeckoLib ──────────────────────────────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        // Controlador del rotor — siempre girando mientras la entidad existe
        registrar.add(new AnimationController<>(this, "rotor", 0, state ->
                state.setAndContinue(ANIM_ROTOR_SPIN)
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

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
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
}
