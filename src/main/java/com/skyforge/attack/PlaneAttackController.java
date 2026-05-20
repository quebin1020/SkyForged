package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Ráfaga de ala fija. El avión apunta con el cuerpo; dispara durante la pasada.
 * Usa la munición asignada a turret_0 y turret_1 alternando en la ráfaga.
 *
 * Si el objetivo está significativamente por debajo y no hay alineación frontal,
 * turret_2 (compartimento de bombas) lanza un HE_SHELL en trayectoria de mortero.
 */
public class PlaneAttackController extends AttackController {

    private static final float  CHARGE       = 5.5f;
    private static final float  SPREAD       = 0.18f;
    private static final double MIN_ALIGN    = 0.96;
    private static final double MAX_RANGE    = 110.0;

    private static final int BURST_SIZE   = 12;
    private static final int BURST_RATE   = 2;
    private static final int RELOAD_TICKS = 70;

    // Bomb drop (turret_2)
    private static final double BOMB_RANGE_H  = 60.0;
    private static final double BOMB_MIN_DY   = 8.0;
    private static final float  BOMB_CHARGE   = 7.0f;
    private static final int    BOMB_COOLDOWN = 100;

    private int burstRemaining = 0;
    private int burstTick      = 0;
    private int burstTurret    = 0;
    private int bombCooldown   = 0;

    public PlaneAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        if (bombCooldown > 0) bombCooldown--;

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

        if (forward.dot(toTarget) >= MIN_ALIGN) {
            burstRemaining = BURST_SIZE;
            burstTick      = 0;
            burstTurret    = 0;
        } else {
            tryBombDrop(target);
        }
    }

    private void tryBombDrop(LivingEntity target) {
        if (bombCooldown > 0) return;

        Vec3 myPos  = platform.getCombatPosition();
        Vec3 tgtPos = target.position();

        double dy = myPos.y - tgtPos.y;
        if (dy < BOMB_MIN_DY) return;

        double dxz = Math.sqrt(
                (myPos.x - tgtPos.x) * (myPos.x - tgtPos.x) +
                (myPos.z - tgtPos.z) * (myPos.z - tgtPos.z));
        if (dxz > BOMB_RANGE_H) return;

        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController bombBay = platform.getAimController(2);
        if (bombBay == null) return;

        Vec3 origin = platform.getTurretOrigin(2);
        Vec3 dir    = tgtPos.add(0, 1, 0).subtract(origin).normalize();
        bombBay.getAmmoType().fire(level, platform.getCombatOwner(), origin, dir, BOMB_CHARGE, 0.03f);
        bombCooldown = BOMB_COOLDOWN;
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
