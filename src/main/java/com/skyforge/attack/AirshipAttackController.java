package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Bombardeo de salva — todas las torretas disparan en secuencia.
 * Cada torreta usa su propia munición asignada (turret.fire()).
 */
public class AirshipAttackController extends AttackController {

    private static final float  CHARGE          = 3.0f;
    private static final float  SPREAD          = 0.15f;
    private static final double MAX_RANGE        = 150;
    private static final int    SALVO_COOLDOWN   = 80;
    private static final int    INTER_SHOT_TICKS = 4;

    private int salvoIndex    = -1;
    private int interShotTick = 0;

    public AirshipAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) { salvoIndex = -1; return; }

        if (salvoIndex >= 0) {
            if (interShotTick > 0) { interShotTick--; return; }

            if (salvoIndex < platform.getTurretCount()) {
                fireFromTurret(salvoIndex);
                salvoIndex++;
                interShotTick = INTER_SHOT_TICKS;
            } else {
                salvoIndex = -1;
                resetCooldown(SALVO_COOLDOWN);
            }
            return;
        }

        if (!canAttack()) return;
        if (platform.getCombatPosition().distanceTo(target.position()) > MAX_RANGE) return;

        salvoIndex    = 0;
        interShotTick = 0;
    }

    private void fireFromTurret(int id) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController turret = platform.getAimController(id);
        if (turret == null || !turret.canShoot()) return;

        // Cada torreta dispara su propia munición asignada
        turret.fire(level, platform.getCombatOwner(), CHARGE, SPREAD);
    }
}
