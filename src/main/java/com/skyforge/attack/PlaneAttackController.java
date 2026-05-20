package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Ráfaga de ala fija. El avión apunta con el cuerpo; dispara durante la pasada.
 * Usa la munición asignada a turret_0 y turret_1 alternando en la ráfaga.
 */
public class PlaneAttackController extends AttackController {

    private static final float  CHARGE       = 5.5f;
    private static final float  SPREAD       = 0.18f;
    private static final double MIN_ALIGN    = 0.96;
    private static final double MAX_RANGE    = 110.0;

    private static final int BURST_SIZE   = 12;
    private static final int BURST_RATE   = 2;
    private static final int RELOAD_TICKS = 70;

    private int burstRemaining = 0;
    private int burstTick      = 0;
    private int burstTurret    = 0;

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
        burstTurret    = 0;
    }

    private void fireBullet() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        // Alterna entre torretas durante la ráfaga
        int id = burstTurret % Math.max(1, platform.getTurretCount());
        burstTurret++;

        AimController turret = platform.getAimController(id);
        if (turret == null) return;

        // Dispara con spread adicional (vuelo frontal, no tracking libre)
        Vec3 dir = platform.getCombatOwner().getLookAngle().add(
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD,
                (level.getRandom().nextDouble() - 0.5) * SPREAD
        ).normalize();

        Vec3 origin = platform.getTurretOrigin(id);
        turret.getAmmoType().fire(level, platform.getCombatOwner(), origin, dir, CHARGE, 0f);
    }
}
