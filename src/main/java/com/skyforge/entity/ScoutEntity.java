package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.ScoutCombatBehavior;
import com.skyforge.attack.ScoutAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.AirplaneMovement;
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
 * Scout — caza de reconocimiento y strafing. El más rápido y ágil.
 * 1 cañón frontal fijo (en el morro). Pasadas rasantes y rápidas.
 */
public class ScoutEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_PROP =
            RawAnimation.begin().thenLoop("animation.scout.prop_spin");

    public ScoutEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torreta única: cañón frontal fijo (morro) ─────────────────────────
        addTurret(0, AimProfile.MACHINE_GUN, CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(0, 0.5, 2.0));
        turretInit();

        // ── Sistemas de vuelo ─────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.scout()));
        this.combatBehavior   = new ScoutCombatBehavior(this);
        this.movement         = new AirplaneMovement(this, FlightConfig.SCOUT);
        this.attackController = new ScoutAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "prop", 0,
                state -> state.setAndContinue(ANIM_PROP)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    20.0)
                .add(Attributes.FLYING_SPEED,  0.65)
                .add(Attributes.MOVEMENT_SPEED, 0.55)
                .add(Attributes.FOLLOW_RANGE,  100.0);
    }
}
