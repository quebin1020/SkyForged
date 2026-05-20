package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.integration.cbc.CBCProjectiles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Airship — bombardeo de salva.
 *
 * Mecánica: dispara todas las torretas casi al mismo tiempo (1 tick entre cada una),
 * luego espera un cooldown largo antes de la siguiente salva.
 * Usa HE shells — destrucción de área desde altitud.
 */
public class AirshipAttackController extends AttackController {

    private static final float  CHARGE          = 3.0f;
    private static final float  SPREAD          = 0.15f; // más spread, es bombardeo
    private static final double MAX_RANGE        = 150;
    private static final int    SALVO_COOLDOWN   = 80;   // 4s entre salvas
    private static final int    INTER_SHOT_TICKS = 4;    // ticks entre cada torreta de la salva

    // índice de torreta pendiente en salva activa (-1 = sin salva)
    private int salvoIndex   = -1;
    private int interShotTick = 0;

    public AirshipAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) { salvoIndex = -1; return; }

        // --- Salva en progreso ---
        if (salvoIndex >= 0) {
            if (interShotTick > 0) { interShotTick--; return; }

            if (salvoIndex < platform.getTurretCount()) {
                fireFromTurret(salvoIndex);
                salvoIndex++;
                interShotTick = INTER_SHOT_TICKS;
            } else {
                // salva completa
                salvoIndex = -1;
                resetCooldown(SALVO_COOLDOWN);
            }
            return;
        }

        // --- Iniciar nueva salva ---
        if (!canAttack()) return;

        double distance = platform.getCombatPosition().distanceTo(target.position());
        if (distance > MAX_RANGE) return;

        salvoIndex    = 0;
        interShotTick = 0;
    }

    private void fireFromTurret(int id) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController turret = platform.getAimController(id);
        if (turret == null || !turret.canShoot()) return;

        Vec3 dir    = turret.applyInaccuracy(turret.getAimDirection());
        Vec3 origin = platform.getTurretOrigin(id);

        CBCProjectiles.fireHEShell(level, platform.getCombatOwner(), origin, dir, CHARGE, SPREAD);
    }
}
