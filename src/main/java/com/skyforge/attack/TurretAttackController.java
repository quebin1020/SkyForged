package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Controlador de torreta estática. Dispara alternando torretas.
 * Usa la munición asignada a cada torreta (turret.fire()).
 */
public class TurretAttackController extends AttackController {

    private static final float  CHARGE    = 3.5f;
    private static final float  SPREAD    = 0.06f;
    private static final double MAX_RANGE = 80.0;
    private static final int    COOLDOWN  = 10;

    private int turretIndex = 0;

    public TurretAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) return;

        if (platform.getCombatPosition().distanceTo(target.position()) > MAX_RANGE) return;
        if (!canAttack()) return;

        // Busca la siguiente torreta que puede disparar
        int count = platform.getTurretCount();
        for (int attempt = 0; attempt < count; attempt++) {
            int id = turretIndex % Math.max(1, count);
            turretIndex++;

            AimController turret = platform.getAimController(id);
            if (turret == null || !turret.canShoot()) continue;

            if (!(platform.getCombatLevel() instanceof ServerLevel level)) break;

            turret.fire(level, platform.getCombatOwner(), CHARGE, SPREAD);
            resetCooldown(COOLDOWN);
            break;
        }
    }
}
