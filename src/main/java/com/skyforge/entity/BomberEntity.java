package com.skyforge.entity;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.BomberAttackController;
import com.skyforge.config.FlightConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.integration.cbc.CBCAmmoType;
import com.skyforge.movement.AirplaneMovement;
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
 * Bombardero de pasada en línea.
 *
 * Vuela en grandes círculos a gran altitud. Cuando cruza sobre el objetivo
 * (dentro de rango horizontal y elevado), el BomberAttackController suelta
 * una salva de 5 bombas HE en secuencia.
 *
 * Torretas:
 *  turret_0  FREE_TRACKING  MG  defensa superior delantera
 *  turret_1  FREE_TRACKING  MG  defensa superior trasera
 *  turret_2  FREE_TRACKING  HE  compartimento de bombas (invisible en modelo)
 */
public class BomberEntity extends AbstractAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_IDLE =
            RawAnimation.begin().thenLoop("animation.bomber.idle");

    private static final FlightConfig BOMBER_FLIGHT = FlightConfig.builder()
            .maxSpeed(0.55f)
            .acceleration(0.05f)
            .turnRate(2.0f)
            .drag(0.93f)
            .requiresForwardMovement(true)
            .minTerrainClearance(35f)
            .build();

    public BomberEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // pivot [0, 40, -80] → Vec3(0, 2.5, 5)  |  pivot [0, 40, 80] → Vec3(0, 2.5, -5)
        addTurret(0, AimProfile.HELICOPTER,                                       CBCAmmoType.MACHINE_GUN_BULLET, new Vec3( 0, 2.5,  5.0));
        addTurret(1, AimProfile.HELICOPTER,                                       CBCAmmoType.MACHINE_GUN_BULLET, new Vec3( 0, 2.5, -5.0));
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.FREE_TRACKING,  CBCAmmoType.HE_SHELL,           new Vec3( 0, -0.5, 0.5));
        turretInit();

        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.plane()));
        this.combatBehavior   = new BomberCombatBehavior(this);
        this.movement         = new AirplaneMovement(this, BOMBER_FLIGHT);
        this.attackController = new BomberAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar r) {
        r.add(new AnimationController<>(this, "idle", 0, state -> state.setAndContinue(ANIM_IDLE)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override protected float getDeathExplosionRadius() { return 4.5f; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,    80.0)
                .add(Attributes.FLYING_SPEED,   0.40)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE,  100.0)
                .add(Attributes.ARMOR,           4.0);
    }

    // ── Comportamiento de pasada: gran círculo a altísima altura ─────────────

    private static class BomberCombatBehavior extends HelicopterCombatBehavior {
        BomberCombatBehavior(BomberEntity entity) {
            super(entity);
            this.orbitRadius     = 120;
            this.preferredHeight = 50;
            this.orbitSpeed      = 0.0025;
        }

        @Override
        public Vec3 getAttackPosition(LivingEntity target) {
            orbitAngle += orbitSpeed;
            return target.position().add(
                    Math.cos(orbitAngle) * orbitRadius,
                    preferredHeight,
                    Math.sin(orbitAngle) * orbitRadius);
        }
    }
}
