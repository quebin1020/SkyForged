package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Armamento del helicóptero — dos cadencias de disparo independientes.
 *
 * PRIMARIA:  Dispara alternando torretas cada MG_COOLDOWN ticks.
 *            Usa la munición asignada a cada torreta (turret.fire()).
 *            Por defecto turret_0 = MG, turret_1 = Solid Shot.
 *
 * SECUNDARIA: La torreta 0 dispara también a cadencia lenta (HEAVY_COOLDOWN).
 *             Útil para la sensación "rat-tat-tat...BOOM".
 *             También usa la munición asignada a turret_0.
 */
public class HelicopterAttackController extends AttackController {

    private static final float  MG_CHARGE     = 6.0f;
    private static final float  MG_SPREAD     = 0.28f;
    private static final int    MG_COOLDOWN   = 4;
    private static final double MG_RANGE      = 70.0;

    private static final float  HEAVY_CHARGE    = 4.0f;
    private static final float  HEAVY_SPREAD    = 0.08f;
    private static final int    HEAVY_COOLDOWN  = 80;
    private static final double HEAVY_RANGE     = 60.0;

    private int heavyCooldown = 0;
    private int turretIndex   = 0;

    public HelicopterAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) return;

        double distance = platform.getCombatPosition().distanceTo(target.position());

        // ── Secundario: disparo pesado de turret_0 ────────────────────────────
        if (heavyCooldown > 0) {
            heavyCooldown--;
        } else if (distance <= HEAVY_RANGE) {
            fireHeavy();
            heavyCooldown = HEAVY_COOLDOWN;
        }

        // ── Primario: alternando torretas ─────────────────────────────────────
        if (!canAttack()) return;
        if (distance > MG_RANGE) return;

        fireMG();
        resetCooldown(MG_COOLDOWN);
    }

    private void fireMG() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        int id = turretIndex % Math.max(1, platform.getTurretCount());
        turretIndex++;

        AimController turret = platform.getAimController(id);
        if (turret == null || !turret.canShoot()) return;

        // Usa la munición asignada a esta torreta
        turret.fire(level, platform.getCombatOwner(), MG_CHARGE, MG_SPREAD);
    }

    private void fireHeavy() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController turret = platform.getAimController(0);
        if (turret == null || !turret.canShoot()) return;

        turret.fire(level, platform.getCombatOwner(), HEAVY_CHARGE, HEAVY_SPREAD);
    }
}
