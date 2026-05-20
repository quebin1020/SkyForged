package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.integration.cbc.CBCProjectiles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Metralladora ligera del scout.
 *
 * Mecánica de ráfaga:
 *   - Espera a que el avión esté alineado con el objetivo (dot > 0.95).
 *   - Dispara una ráfaga de BURST_SIZE balas a BURST_RATE ticks/bala.
 *   - Al acabar la ráfaga, recarga durante RELOAD_TICKS.
 *
 * El avión debe hacer otro pase para disparar de nuevo — así se siente
 * como una pasada de strafing real.
 */
public class ScoutAttackController extends AttackController {

    private static final float  CHARGE       = 5.0f;  // bala rápida
    private static final float  SPREAD       = 0.3f;  // spread tipo ametralladora
    private static final float  MIN_ALIGN    = 0.95f; // ~18° de cono de disparo
    private static final double MAX_RANGE    = 80.0;

    private static final int BURST_SIZE   = 8;   // balas por ráfaga
    private static final int BURST_RATE   = 3;   // ticks entre balas en ráfaga
    private static final int RELOAD_TICKS = 80;  // recarga entre ráfagas (~4s)

    private int burstRemaining = 0;
    private int burstTick      = 0;

    public ScoutAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) { burstRemaining = 0; return; }

        // ── Continuar ráfaga activa ──
        if (burstRemaining > 0) {
            if (burstTick > 0) { burstTick--; return; }

            fireBullet();
            burstRemaining--;
            burstTick = BURST_RATE;

            if (burstRemaining == 0) {
                resetCooldown(RELOAD_TICKS);
            }
            return;
        }

        // ── Condiciones para iniciar ráfaga ──
        if (!canAttack()) return;

        double distance = platform.getCombatPosition().distanceTo(target.position());
        if (distance > MAX_RANGE) return;

        Vec3 forward  = platform.getCombatOwner().getLookAngle();
        Vec3 toTarget = target.position().subtract(platform.getCombatPosition()).normalize();
        if (forward.dot(toTarget) < MIN_ALIGN) return;

        // Iniciar ráfaga
        burstRemaining = BURST_SIZE;
        burstTick      = 0;
    }

    private void fireBullet() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController turret = platform.getAimController(0);
        if (turret == null) return;

        Vec3 dir    = turret.applyInaccuracy(turret.getAimDirection());
        Vec3 origin = platform.getTurretOrigin(0);

        CBCProjectiles.fireMachineGunBullet(level, platform.getCombatOwner(), origin, dir, CHARGE, SPREAD);
    }
}
