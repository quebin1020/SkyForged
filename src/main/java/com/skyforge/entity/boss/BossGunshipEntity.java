package com.skyforge.entity.boss;

import com.skyforge.ai.AIStateMachine;
import com.skyforge.ai.PatrolNavigator;
import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.ai.combat.HelicopterCombatBehavior;
import com.skyforge.attack.BossGunshipAttackController;
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
 * Dreadnought Mk-I — boss aéreo pesado de triple escala.
 *
 * ── Torretas ────────────────────────────────────────────────────────────────
 *  turret_0  FREE_TRACKING  MACHINE_GUN_BULLET  cúpula superior delantera
 *  turret_1  FREE_TRACKING  MACHINE_GUN_BULLET  cúpula superior trasera
 *  turret_2  LIMITED_TURN   AP_SHELL            costado izq. frente
 *  turret_3  LIMITED_TURN   AP_SHELL            costado izq. centro
 *  turret_4  LIMITED_TURN   AP_SHELL            costado izq. popa
 *  turret_5  LIMITED_TURN   AP_SHELL            costado der. frente
 *  turret_6  LIMITED_TURN   AP_SHELL            costado der. centro
 *  turret_7  LIMITED_TURN   AP_SHELL            costado der. popa
 *
 * ── Fases ────────────────────────────────────────────────────────────────
 *  Fase 1 (100 %):  MG + AP_SHELL
 *  Fase 2 ( 65 %):  AP_AUTOCANNON  ×1.35 velocidad
 *  Fase 3 ( 30 %):  AP_AUTOCANNON  ×1.7  velocidad — "ALL WEAPONS FREE"
 *
 * ── Partes rompibles ─────────────────────────────────────────────────────
 *  Cada torreta tiene su propia HP. Destruirla desactiva el cañón.
 *  Llamar damagePartDirect("ap_l_front", 50f) desde un sistema de hit-detection.
 *
 * ── Escala ───────────────────────────────────────────────────────────────
 *  Hitbox 21 × 11 bloques (triple del gunship normal 7 × 4).
 *  Modelo: fuselaje 12.5×5×20 bloques, alas hasta ±12 bloques a cada lado.
 */
public class BossGunshipEntity extends AbstractBossAerialEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation ANIM_IDLE =
            RawAnimation.begin().thenLoop("animation.boss_gunship.engine_idle");

    private static final FlightConfig BOSS_FLIGHT = FlightConfig.builder()
            .maxSpeed(0.18f)
            .acceleration(0.012f)
            .turnRate(1.2f)
            .drag(0.994f)
            .canRotateInPlace(true)
            .minTerrainClearance(30f)
            .build();

    public BossGunshipEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas ──────────────────────────────────────────────────────────
        // pivot geo / 16 → Vec3  (X misma señal, Z invertida: entity_z = -pivot_z/16)
        addTurret(0, AimProfile.HELICOPTER,                                  CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(  0.0, 5.0,  7.5));
        addTurret(1, AimProfile.HELICOPTER,                                  CBCAmmoType.MACHINE_GUN_BULLET, new Vec3(  0.0, 5.0, -7.5));
        addTurret(2, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3(-7.5, 1.5,  5.0));
        addTurret(3, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3(-7.5, 1.5,  0.0));
        addTurret(4, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3(-7.5, 1.5, -5.0));
        addTurret(5, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3( 7.5, 1.5,  5.0));
        addTurret(6, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3( 7.5, 1.5,  0.0));
        addTurret(7, AimProfile.HELICOPTER, AimController.AimMode.LIMITED_TURN, CBCAmmoType.AP_SHELL, new Vec3( 7.5, 1.5, -5.0));
        turretInit();

        // ── Partes rompibles ──────────────────────────────────────────────────
        addBossPart("mg_front",   "turret_0", 0, 200f);
        addBossPart("mg_rear",    "turret_1", 1, 200f);
        addBossPart("ap_l_front", "turret_2", 2, 150f);
        addBossPart("ap_l_mid",   "turret_3", 3, 150f);
        addBossPart("ap_l_rear",  "turret_4", 4, 150f);
        addBossPart("ap_r_front", "turret_5", 5, 150f);
        addBossPart("ap_r_mid",   "turret_6", 6, 150f);
        addBossPart("ap_r_rear",  "turret_7", 7, 150f);

        // ── Fases ─────────────────────────────────────────────────────────────
        initBoss("Dreadnought Mk-I",
                BossPhase.start(),
                BossPhase.escalate(0.65f, CBCAmmoType.AP_AUTOCANNON, 1.35f, 0.75f,
                        "§cDreadnought: Secondary batteries online!"),
                BossPhase.escalate(0.30f, CBCAmmoType.AP_AUTOCANNON, 1.70f, 0.55f,
                        "§4Dreadnought: CRITICAL DAMAGE — ALL WEAPONS FREE!")
        );

        // ── Sistemas ──────────────────────────────────────────────────────────
        this.targeting        = new TargetingSystem(this);
        this.brain            = new AIStateMachine(this, new PatrolNavigator(this, PatrolPresets.helicopter()));
        this.combatBehavior   = new BossCombatBehavior(this);
        this.movement         = new HelicopterMovement(this, BOSS_FLIGHT);
        this.attackController = new BossGunshipAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "idle", 0,
                state -> state.setAndContinue(ANIM_IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,       1200.0)
                .add(Attributes.FLYING_SPEED,        0.12)
                .add(Attributes.MOVEMENT_SPEED,      0.10)
                .add(Attributes.FOLLOW_RANGE,       160.0)
                .add(Attributes.ARMOR,               20.0)
                .add(Attributes.ARMOR_TOUGHNESS,      8.0);
    }

    // ── Comportamiento orbital amplio ─────────────────────────────────────────

    private static class BossCombatBehavior extends HelicopterCombatBehavior {
        BossCombatBehavior(BossGunshipEntity entity) {
            super(entity);
            this.orbitRadius     = 90;
            this.preferredHeight = 38;
            this.orbitSpeed      = 0.002;
        }
    }
}
