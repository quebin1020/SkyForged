package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class HelicopterAttackController
        extends AttackController {

    public HelicopterAttackController(
            CombatPlatform platform
    ) {

        super(platform);
    }

    @Override
    protected void attackTick() {

        LivingEntity target = getTarget();

        if (target == null)
            return;

        double distance = platform.getCombatPosition()
                .distanceTo(target.position());

        if (distance > 60)
            return;

        for (int id = 0; id < 2; id++) { // o turrets.size()

            AimController turret = platform.getAimController(id);

            if (turret == null)
                continue;

            if (!turret.canShoot())
                continue;

            Vec3 direction = turret.getAimDirection();
            direction = turret.applyInaccuracy(direction);

            if (cooldown > 0)
                continue;

            resetCooldown(20);

            Vec3 origin = platform.getTurretOrigin(id);

            SmallFireball fireball = new SmallFireball(
                    platform.getCombatLevel(),
                    origin.x,
                    origin.y,
                    origin.z,
                    direction.scale(2)
            );

            fireball.setOwner(platform.getCombatOwner());

            platform.spawnCombatEntity(fireball);

            break; // opcional: evita que todos disparen al mismo tick
        }
    }

}