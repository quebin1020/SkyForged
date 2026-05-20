package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.PlaneCombatBehavior;
import com.skyforge.attack.PlaneAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.AirplaneMovement;
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

public class PlaneEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_PROP =
            RawAnimation.begin().thenLoop("animation.airplane.prop_spin");

    public PlaneEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas (cañones fijos, avión apunta con el cuerpo) ──────────────
        // turret_0: cañón de ala izq — MG pesada
        // turret_1: cañón de ala der — AP (penetración)
        addTurret(0, AimProfile.AIRPLANE, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(-3.0, 0.5, 1.0));
        addTurret(1, AimProfile.AIRPLANE, CBCAmmoType.AP_SHOT,            new Vec3( 3.0, 0.5, 1.0));
        turretInit();

        // ── Sistemas de vuelo ─────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.plane()));
        this.combatBehavior   = new PlaneCombatBehavior(this);
        this.movement         = new AirplaneMovement(this, FlightConfig.PLANE);
        this.attackController = new PlaneAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "prop", 0,
                state -> state.setAndContinue(ANIM_PROP)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        if (brain != null && tickCount % 3 == 0 && brain.getPatrolTarget() != null) {
            DebugRender.drawLine(level(), this.position(), brain.getPatrolTarget());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    30.0)
                .add(Attributes.FLYING_SPEED,   0.45)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE,  80.0);
    }
}
