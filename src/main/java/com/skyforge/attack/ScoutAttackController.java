package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Ráfaga del scout — cañón frontal fijo, dispara durante la pasada.
 * Usa la munición de turret_0 (por defecto MACHINE_GUN_BULLET).
 */
public class ScoutAttackController extends AttackController {

    private static final float  CHARGE       = 5.0f;
    private static final float  SPREAD       = 0.3f;
    private static final float  MIN_ALIGN    = 0.95f;
    private static final double MAX_RANGE    = 80.0;

    private static final int BURST_SIZE   = 8;
    private static final int BURST_RATE   = 3;
    private static final int RELOAD_TICKS = 80;

    private int burstRemaining = 0;
    private int burstTick      = 0;

    public ScoutAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) { burstRemaining = 0; return; }

        if (burstRemaining > 0) {
            if (burstTick > 0) { burstTick--; return; }
            fireBullet();
            burstRemaining--;
            burstTick = BURST_RATE;
            if (burstRemaining == 0) resetCooldown(RELOAD_TICKS);
            return;
        }

        if (!canAttack()) return;

        double distance = platform.getCombatPosition().distanceTo(target.position());
        if (distance > MAX_RANGE) return;

        Vec3 forward  = platform.getCombatOwner().getLookAngle();
        Vec3 toTarget = target.position().subtract(platform.getCombatPosition()).normalize();
        if (forward.dot(toTarget) < MIN_ALIGN) return;

        burstRemaining = BURST_SIZE;
        burstTick      = 0;
    }

    private void fireBullet() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController turret = platform.getAimController(0);
        if (turret == null) return;

        Vec3 dir = platform.getCombatOwner().getLookAngle().add(
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD
        ).normalize();

        Vec3 origin = platform.getTurretOrigin(0);
        turret.getAmmoType().fire(level, platform.getCombatOwner(), origin, dir, CHARGE, 0f);
    }
}
