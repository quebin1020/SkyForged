package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.AirshipCombatBehavior;
import com.skyforge.attack.AirshipAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.AirshipMovement;
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

public class AirshipEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ENGINE =
            RawAnimation.begin().thenLoop("animation.airship.engine_idle");

    public AirshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas (LIMITED_TURN — el dirigible es rígido) ─────────────────
        // turret_0: babor — HE bombardeo
        // turret_1: estribor — HE bombardeo
        // turret_2: proa — AP penetración
        // turret_3: popa — Solid Shot defensivo
        addTurret(0, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.HE_SHELL,  new Vec3(-4.5, 1.0,  0.0));
        addTurret(1, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.HE_SHELL,  new Vec3( 4.5, 1.0,  0.0));
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL,  new Vec3( 0.0, 1.0,  5.0));
        addTurret(3, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.SOLID_SHOT,new Vec3( 0.0, 1.0, -5.0));
        turretInit();

        // ── Sistemas de vuelo ─────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.boat()));
        this.combatBehavior   = new AirshipCombatBehavior(this);
        this.movement         = new AirshipMovement(this, FlightConfig.AIRSHIP);
        this.attackController = new AirshipAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "engine", 0,
                state -> state.setAndContinue(ANIM_ENGINE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        if (brain != null && tickCount % 10 == 0 && brain.getPatrolTarget() != null) {
            DebugRender.drawLine(level(), this.position(), brain.getPatrolTarget());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    60.0)
                .add(Attributes.FLYING_SPEED,  0.15)
                .add(Attributes.MOVEMENT_SPEED, 0.10)
                .add(Attributes.FOLLOW_RANGE,  96.0);
    }
}
