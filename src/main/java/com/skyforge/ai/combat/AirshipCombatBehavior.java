package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AirshipCombatBehavior implements CombatBehavior {

    protected final AbstractAerialEntity entity;

    protected double preferredDistance = 70;
    protected double preferredHeight = 25;

    protected double strafeAngle = 0;
    protected double strafeSpeed = 0.002;

    public AirshipCombatBehavior(AbstractAerialEntity entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 getAttackPosition(LivingEntity target) {

        strafeAngle += strafeSpeed;

        Vec3 toTarget = target.position()
                .subtract(entity.position())
                .normalize();

        Vec3 perpendicular = new Vec3(
                -toTarget.z,
                0,
                toTarget.x
        );

        Vec3 offset =
                toTarget.scale(-preferredDistance)
                        .add(perpendicular.scale(Math.sin(strafeAngle) * 10));

        return target.position().add(
                offset.x,
                preferredHeight,
                offset.z
        );
    }
}