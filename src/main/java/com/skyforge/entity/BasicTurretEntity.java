package com.skyforge.entity;

import com.skyforge.ai.combat.AimProfile;
import com.skyforge.attack.TurretAttackController;
import com.skyforge.integration.cbc.CBCAmmoType;
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
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Torreta estática de dos cañones. Sin movimiento.
 * turret_0: cañón delantero — HE shell
 * turret_1: cañón trasero  — Solid Shot defensivo
 */
public class BasicTurretEntity extends AbstractTurretEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BasicTurretEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);

        // ── Torretas ──────────────────────────────────────────────────────────
        // Perfil TURRET: aimSpeed 0.08, projectileSpeed 3.0, tolerance 6°, inaccuracy 0.03
        addTurret(0, AimProfile.TURRET, CBCAmmoType.HE_SHELL,   new Vec3(0, 1.5,  0.5));
        addTurret(1, AimProfile.TURRET, CBCAmmoType.SOLID_SHOT, new Vec3(0, 1.5, -0.5));
        turretInit();

        this.targeting        = new TargetingSystem(this);
        this.attackController = new TurretAttackController(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        // Sin animación de movimiento — la rotación la controla GeckoLib via EntityData
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,   40.0)
                .add(Attributes.FOLLOW_RANGE, 100.0);
    }
}
