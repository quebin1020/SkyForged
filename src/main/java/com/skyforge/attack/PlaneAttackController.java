package com.skyforge.attack;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class PlaneAttackController extends AttackController {

    public PlaneAttackController(CombatPlatform platform) {
        super(platform);
    }

    @Override
    protected void attackTick() {

        LivingEntity target = getTarget();

        if (target == null)
            return;

        if (!canAttack())
            return;

        Vec3 forward = platform.getCombatOwner().getLookAngle();
        Vec3 toTarget = target.position().subtract(platform.getCombatPosition()).normalize();

        double dot = forward.dot(toTarget);

        if(dot < 0.98) return; // casi frente

        double distance = platform.getCombatPosition()
                .distanceTo(target.position());

        if (distance > 100)
            return;

        // solo un cañón activo
        AimController turret = platform.getAimController(0);

        if (turret == null)
            return;

        if (!turret.canShoot())
            return;

        resetCooldown(8); // más agresivo

        Vec3 dir = turret.applyInaccuracy(
                turret.getAimDirection()
        );

        Vec3 origin = platform.getTurretOrigin(0);

        SmallFireball fireball = new SmallFireball(
                platform.getCombatLevel(),
                origin.x,
                origin.y,
                origin.z,
                dir.scale(3)
        );

        fireball.setOwner(platform.getCombatOwner());
        platform.spawnCombatEntity(fireball);
    }

}