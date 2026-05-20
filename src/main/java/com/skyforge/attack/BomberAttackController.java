package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Ataque del bombardero: pasada en línea.
 *
 * Cuando el bombardero está dentro de BOMB_RANGE_H (horizontal)
 * Y al menos BOMB_MIN_DY bloques por encima del objetivo,
 * suelta hasta SALVO_SIZE bombas HE con BOMB_RATE ticks entre cada una.
 * Luego entra en cooldown antes de la siguiente pasada.
 */
public class BomberAttackController extends AttackController {

    private static final float  BOMB_CHARGE   = 8.0f;
    private static final double BOMB_RANGE_H  = 55.0;
    private static final double BOMB_MIN_DY   = 20.0;
    private static final int    SALVO_SIZE    = 5;
    private static final int    BOMB_RATE     = 8;
    private static final int    RELOAD_TICKS  = 220;

    private int salvoLeft = 0;
    private int bombTick  = 0;

    public BomberAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {
        LivingEntity target = getTarget();
        if (target == null) { salvoLeft = 0; return; }

        if (salvoLeft > 0) {
            if (bombTick > 0) { bombTick--; return; }
            dropBomb(target);
            salvoLeft--;
            bombTick = BOMB_RATE;
            if (salvoLeft == 0) resetCooldown(RELOAD_TICKS);
            return;
        }

        if (!canAttack()) return;

        Vec3 myPos  = platform.getCombatPosition();
        Vec3 tgtPos = target.position();

        double dy  = myPos.y - tgtPos.y;
        if (dy < BOMB_MIN_DY) return;

        double dxz = Math.sqrt(
                (myPos.x - tgtPos.x) * (myPos.x - tgtPos.x) +
                (myPos.z - tgtPos.z) * (myPos.z - tgtPos.z));
        if (dxz > BOMB_RANGE_H) return;

        salvoLeft = SALVO_SIZE;
        bombTick  = 0;
    }

    private void dropBomb(LivingEntity target) {
        if (!(platform.getCombatLevel() instanceof ServerLevel level)) return;

        AimController bay = platform.getAimController(2);
        if (bay == null) return;

        Vec3 origin = platform.getTurretOrigin(2);
        Vec3 dir    = target.position().add(0, 1, 0).subtract(origin).normalize();
        bay.getAmmoType().fire(level, platform.getCombatOwner(), origin, dir, BOMB_CHARGE, 0.02f);
    }
}
