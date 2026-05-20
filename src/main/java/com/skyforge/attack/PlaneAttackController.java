package com.skyforge.attack;

import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.integration.cbc.CBCProjectiles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Nose guns de avión pesado — ráfaga sostenida de metralladora durante la pasada.
 *
 * Diferencia vs ScoutAttackController:
 *   Plane  — 12 rondas, 2 ticks entre rondas, carga 5.5 → fuego más denso y continuo
 *   Scout  —  8 rondas, 3 ticks entre rondas, carga 4.5 → más ágil pero menos volumen
 *
 * Ciclo: el PlaneCombatBehavior posiciona al avión en el FIRE_RUN.
 * Cuando el morro apunta al objetivo (dot > 0.96) se abre la ráfaga.
 * Tras la ráfaga hay una recarga larga mientras el avión vuelve a APPROACH.
 */
public class PlaneAttackController extends AttackController {

    // Metralladora ligera — misma munición que el scout pero más volumen
    private static final float  CHARGE       = 5.5f;
    private static final float  SPREAD       = 0.18f;
    private static final double MIN_ALIGN    = 0.96;   // ~16°
    private static final double MAX_RANGE    = 110.0;

    private static final int BURST_SIZE   = 12;  // más balas que el scout (8)
    private static final int BURST_RATE   = 2;   // más rápido que el scout (3)
    private static final int RELOAD_TICKS = 70;

    private int burstRemaining = 0;
    private int burstTick      = 0;

    public PlaneAttackController(CombatPlatform platform) {
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

        Vec3 dir = platform.getCombatOwner().getLookAngle().add(
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD
        ).normalize();

        CBCProjectiles.fireMachineGunBullet(level, platform.getCombatOwner(),
                platform.getTurretOrigin(0), dir, CHARGE, 0f);
    }
}
