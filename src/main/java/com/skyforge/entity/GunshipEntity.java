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

/**
 * Gunship — helicóptero de combate pesado.
 *
 * Stats vs DebugHelicopterEntity:
 *   HP:         80  (vs 20)
 *   Hitbox:     5.0 x 2.5  (vs 3.5 x 1.8)
 *   Velocidad:  ligeramente mayor
 *   Órbita:     radio más grande (60 bloques)
 *   Rango MG:   90 bloques
 */
public class GunshipEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_ROTOR_SPIN =
            RawAnimation.begin().thenLoop("animation.helicopter.rotor_spin");

    public GunshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        initTurrets();
        for (AimController turret : turrets.values()) {
            turret.setMode(AimController.AimMode.FREE_TRACKING);
        }

        FlightConfig config = new FlightConfig(
                0.42f,   // maxSpeed — un poco más que el debug
                0.035f,  // acceleration
                5f,      // turnRate
                0.025f,  // climbRate
                0.978f,  // drag
                true,
                false
        );

        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new GunshipCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, config);
        this.attackController = new HelicopterAttackController(this);
    }

    // ── GeckoLib ─────────────────────────────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "rotor", 0, state ->
                state.setAndContinue(ANIM_ROTOR_SPIN)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ── Atributos ─────────────────────────────────────────────────────────────

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    80.0)
                .add(Attributes.FLYING_SPEED,  0.35)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.FOLLOW_RANGE,  96.0);
    }

    // ── Comportamiento orbital más amplio ─────────────────────────────────────

    private static class GunshipCombatBehavior extends HelicopterCombatBehavior {
        GunshipCombatBehavior(GunshipEntity entity) {
            super(entity);
            this.orbitRadius    = 60;   // más espacio que el debug (45)
            this.preferredHeight = 22;  // más alto
            this.orbitSpeed     = 0.006; // más lento y majestuoso
        }
    }
}
