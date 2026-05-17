package com.skyforge.attack;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

import static com.skyforge.SkyforgeMod.LOGGER1;

public class HelicopterAttackController extends AttackController {
    public HelicopterAttackController(AbstractAerialEntity entity) {
        super(entity);
    }
    @Override
    protected void attackTick() {

        LivingEntity target = entity.getTargetingSystem()
                .getTarget();

        if(target == null) return;

        double distance =
                entity.position()
                        .distanceTo(
                                target.position()
                        );

        if(distance > 60) return;

        if(!canAttack()) return;

        resetCooldown(20);

        Vec3 direction =
                target.position()
                        .subtract(entity.position())
                        .normalize()
                        .scale(2);

        SmallFireball fireball =
                new SmallFireball(
                        entity.level(),
                        entity.position().x,
                        entity.position().y + 2,
                        entity.position().z,
                        direction
                );

        fireball.setOwner(entity);

        entity.level()
                .addFreshEntity(fireball);
    }

}
