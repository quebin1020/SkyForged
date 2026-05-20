package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Controlador del helicóptero de misiles.
 *
 * MG (turret_0, turret_1): ráfaga rápida cuando el aim está listo.
 * Misiles (turret_2..5): salva de 2 misiles (un pod por costado)
 *   cuando el helicóptero está suficientemente alineado con el objetivo.
 *   Cooldown largo entre salvas.
 */
public class MissileHelicopterAttackController extends AttackController {

    private static final float MG_CHARGE  = 3.0f;
    private static final float MSL_CHARGE = 9.0f;

    // MG burst
    private static final int MG_BURST      = 8;
    private static final int MG_BURST_RATE = 3;
    private static final int MG_RELOAD     = 45;
    private int mgBurstLeft = 0;
    private int mgBurstTick = 0;
    private int mgCooldown  = 0;

    // Missile salvo
    private static final int MSL_RELOAD   = 100;
    private static final int MSL_PAIR_GAP = 6;
    private int mslCooldown  = 0;
    private int mslPairIndex = 0;  // 0 or 1 → fires (2,4) then (3,5)
    private int mslTick      = 0;

    public MissileHelicopterAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) {
            mgBurstLeft  = 0;
            mslPairIndex = 0;
            return;
        }
        tickMG();
        tickMissiles();
    }

    // ── MG ────────────────────────────────────────────────────────────────────

    private void tickMG() {
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

    // ── Missiles ──────────────────────────────────────────────────────────────

    private void tickMissiles() {
        if (mslCooldown > 0) { mslCooldown--; return; }

        if (mslPairIndex < 2) {
            if (mslTick > 0) { mslTick--; return; }
            fireMissilePair(mslPairIndex);
            mslPairIndex++;
            mslTick = MSL_PAIR_GAP;
            if (mslPairIndex >= 2) {
                mslCooldown  = MSL_RELOAD;
                mslPairIndex = 0;
            }
            return;
        }

        // Check if any pod is aligned enough to launch
        AimController pod2 = platform.getAimController(2);
        AimController pod4 = platform.getAimController(4);
        if ((pod2 != null && pod2.canShoot()) || (pod4 != null && pod4.canShoot())) {
            mslPairIndex = 0;
            mslTick      = 0;
        }
    }

    // pair 0 = (turret_2 + turret_4), pair 1 = (turret_3 + turret_5)
    private void fireMissilePair(int pairIndex) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;
        int[] ids = pairIndex == 0 ? new int[]{2, 4} : new int[]{3, 5};
        for (int id : ids) {
            AimController t = platform.getAimController(id);
            if (t != null) t.fire(level, platform.getCombatOwner(), MSL_CHARGE, 0.01f);
        }
    }
}
