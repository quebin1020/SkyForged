package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.integration.cbc.CBCProjectiles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Armamento del helicóptero — dos sistemas independientes:
 *
 *  PRIMARIO  — Metralladora de puerta (MG rounds).
 *              Fuego rápido, cada 4 ticks, desde las torretas FREE_TRACKING.
 *              Cada torreta dispara alternada (turret 0 → turret 1 → ...).
 *              Carga alta (6.0) para sensación de impacto.
 *
 *  SECUNDARIO — Cañón sólido (solid shot).
 *              Golpe pesado cada ~80 ticks (4 segundos).
 *              Una sola ronda pero con mucha fuerza (charge 4.0).
 *              Actúa como el "thump" entre ráfagas de MG.
 *
 * El ritmo resultante: rat-tat-tat-tat ... BOOM ... rat-tat-tat ...
 */
public class HelicopterAttackController extends AttackController {

    // Metralladora (primaria)
    private static final float  MG_CHARGE      = 6.0f;
    private static final float  MG_SPREAD      = 0.28f;
    private static final int    MG_COOLDOWN    = 4;
    private static final double MG_RANGE       = 70.0;

    // Solid shot (secundario)
    private static final float  SHOT_CHARGE    = 4.0f;
    private static final float  SHOT_SPREAD    = 0.08f;
    private static final int    SHOT_COOLDOWN  = 80;
    private static final double SHOT_RANGE     = 60.0;

    // Cooldown del primario se maneja con el cooldown base del AttackController
    // El secundario tiene su propio contador
    private int  heavyCooldown  = 0;
    private int  turretIndex    = 0;   // alterna entre torretas para el MG

    public HelicopterAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) return;

        double distance = platform.getCombatPosition().distanceTo(target.position());

        // ── Secundario: solid shot (timer independiente) ──
        if (heavyCooldown > 0) {
            heavyCooldown--;
        } else if (distance <= SHOT_RANGE) {
            fireHeavy(target);
            heavyCooldown = SHOT_COOLDOWN;
        }

        // ── Primario: MG rápida ──
        if (!canAttack()) return;
        if (distance > MG_RANGE) return;

        fireMG();
        resetCooldown(MG_COOLDOWN);
    }

    private void fireMG() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        // Alterna entre torretas para dar sensación de disparo bilateral
        int id = turretIndex % platform.getTurretCount();
        turretIndex++;

        AimController turret = platform.getAimController(id);
        if (turret == null || !turret.canShoot()) return;

        Vec3 dir    = turret.applyInaccuracy(turret.getAimDirection());
        Vec3 origin = platform.getTurretOrigin(id);

        CBCProjectiles.fireMachineGunBullet(level, platform.getCombatOwner(), origin, dir, MG_CHARGE, MG_SPREAD);
    }

    private void fireHeavy(LivingEntity target) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        // El solid shot siempre desde la torreta 0 (cañón principal)
        AimController turret = platform.getAimController(0);
        if (turret == null || !turret.canShoot()) return;

        Vec3 dir    = turret.applyInaccuracy(turret.getAimDirection());
        Vec3 origin = platform.getTurretOrigin(0);

        CBCProjectiles.fireSolidShot(level, platform.getCombatOwner(), origin, dir, SHOT_CHARGE, SHOT_SPREAD);
    }
}
