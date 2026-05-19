package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class AirshipAttackController extends AttackController {

    private int salvoCounter = 0;

    public AirshipAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {

        LivingEntity target = getTarget();

        if (target == null)
            return;

        if (!canAttack())
            return;

        if (salvoCounter > 0) {
            salvoCounter--;
            return;
        }

        for (int id = 0; id < platform.getTurretCount(); id++) {

            AimController turret = platform.getAimController(id);

            if (turret == null || !turret.canShoot())
                continue;

            Vec3 dir = turret.applyInaccuracy(turret.getAimDirection());
            Vec3 origin = platform.getTurretOrigin(id);

            SmallFireball fireball = new SmallFireball(
                    platform.getCombatLevel(),
                    origin.x,
                    origin.y,
                    origin.z,
                    dir.scale(2)
            );

            fireball.setOwner(platform.getCombatOwner());
            platform.spawnCombatEntity(fireball);
        }

        resetCooldown(40);
        salvoCounter = 10; // delay entre salvas
    }
}