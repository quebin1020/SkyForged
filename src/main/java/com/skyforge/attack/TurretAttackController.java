package com.skyforge.attack;

import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class TurretAttackController
        extends AttackController {

    public TurretAttackController(
            CombatPlatform platform
    ) {

        super(platform);
    }

    @Override
    protected void attackTick() {

        LivingEntity target =
                getTarget();

        if(target == null)
            return;

        double distance =
                platform.getCombatPosition()
                        .distanceTo(
                                target.position()
                        );

        if(distance > 80)
            return;

        if(!canAttack())
            return;

        if(!platform.getAimController()
                .canShoot())
            return;

        resetCooldown(10);

        Vec3 direction =
                platform.getAimController()
                        .getAimDirection();

        direction =
                platform.getAimController()
                        .applyInaccuracy(
                                direction
                        );

        direction =
                direction.scale(3);

        SmallFireball fireball =
                new SmallFireball(

                        platform.getCombatLevel(),

                        platform.getAimOrigin().x,

                        platform.getAimOrigin().y,

                        platform.getAimOrigin().z,

                        direction
                );

        fireball.setOwner(
                platform.getCombatOwner()
        );

        platform.spawnCombatEntity(
                fireball
        );
    }
}