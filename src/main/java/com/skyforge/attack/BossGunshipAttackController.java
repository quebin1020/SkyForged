package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.entity.boss.BossGunshipEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Controlador de ataque para el Dreadnought Mk-I.
 *
 * Grupo A — MG (turret_0, turret_1): ráfagas cuando el aim está listo.
 * Grupo B — Broadside AP (turret_2..7): volleys alternados izq/der.
 *
 * Fases:
 *   Fase 0 — interval=90  solo alternado
 *   Fase 1 — interval=55  solo alternado (AP_AUTOCANNON ya activo por ammo override)
 *   Fase 2+ — interval=30  AMBOS costados simultáneamente
 */
public class BossGunshipAttackController extends AttackController {

    private static final float MG_CHARGE = 3.0f;
    private static final float AP_CHARGE = 8.5f;

    // MG burst
    private static final int MG_BURST      = 6;
    private static final int MG_BURST_RATE = 3;
    private static final int MG_RELOAD     = 50;
    private int mgBurstLeft = 0;
    private int mgBurstTick = 0;
    private int mgCooldown  = 0;

    // Broadside volley
    private static final int CANNON_GAP = 14;
    private int  volleyTimer   = 0;
    private int  cannonInQueue = 0;
    private int  cannonTick    = 0;
    private boolean leftSide   = true;

    public BossGunshipAttackController(CombatPlatform platform) {
        super(platform);
    }

    // ── Phase helpers ─────────────────────────────────────────────────────────

    private BossGunshipEntity getBoss() {
        return platform.getCombatOwner() instanceof BossGunshipEntity b ? b : null;
    }

    private int getVolleyInterval() {
        BossGunshipEntity b = getBoss();
        if (b == null) return 90;
        return switch (b.getCurrentPhase()) {
            case 0 -> 90;
            case 1 -> 55;
            default -> 30;
        };
    }

    private boolean shouldFireBothSides() {
        BossGunshipEntity b = getBoss();
        return b != null && b.getCurrentPhase() >= 2;
    }

    // ── Main tick ─────────────────────────────────────────────────────────────

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) {
            mgBurstLeft   = 0;
            cannonInQueue = 0;
            return;
        }
        tickMgBurst();
        tickBroadside();
    }

    // ── MG ────────────────────────────────────────────────────────────────────

    private void tickMgBurst() {
        if (mgCooldown > 0) { mgCooldown--; return; }

        if (mgBurstLeft > 0) {
            if (mgBurstTick > 0) { mgBurstTick--; return; }
            fireMG();
            mgBurstLeft--;
            mgBurstTick = MG_BURST_RATE;
            if (mgBurstLeft == 0) mgCooldown = MG_RELOAD;
            return;
        }

        AimController mg0 = platform.getAimController(0);
        AimController mg1 = platform.getAimController(1);
        if ((mg0 != null && mg0.canShoot()) || (mg1 != null && mg1.canShoot())) {
            mgBurstLeft = MG_BURST;
            mgBurstTick = 0;
        }
    }

    private void fireMG() {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;
        for (int id : new int[]{0, 1}) {
            AimController t = platform.getAimController(id);
            if (t != null && t.canShoot()) t.fire(level, platform.getCombatOwner(), MG_CHARGE, 0f);
        }
    }

    // ── Broadside AP ──────────────────────────────────────────────────────────

    private void tickBroadside() {
        if (cannonInQueue < 3) {
            if (cannonTick > 0) { cannonTick--; return; }
            // Fire current side
            fireSideCannon(cannonInQueue, leftSide);
            // Phase 2+: fire both sides simultaneously
            if (shouldFireBothSides()) {
                fireSideCannon(cannonInQueue, !leftSide);
            }
            cannonInQueue++;
            cannonTick = CANNON_GAP;
        }

        volleyTimer++;
        if (volleyTimer >= getVolleyInterval()) {
            volleyTimer   = 0;
            cannonInQueue = 0;
            cannonTick    = 0;
            leftSide      = !leftSide;
        }
    }

    private void fireSideCannon(int indexInSide, boolean left) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;
        // turrets 2,3,4 = left;  5,6,7 = right
        int id = left ? (2 + indexInSide) : (5 + indexInSide);
        AimController turret = platform.getAimController(id);
        if (turret == null) return;
        turret.fire(level, platform.getCombatOwner(), AP_CHARGE, 0f);
    }
}
