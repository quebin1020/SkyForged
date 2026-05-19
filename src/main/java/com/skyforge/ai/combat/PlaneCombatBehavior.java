package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PlaneCombatBehavior implements CombatBehavior {

    protected final AbstractAerialEntity entity;

    protected double leadStrength = 1.2;
    protected double diveHeight = 5;

    public PlaneCombatBehavior(AbstractAerialEntity entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 getAttackPosition(LivingEntity target) {

        Vec3 targetPos = target.position();
        Vec3 velocity = target.getDeltaMovement();

        Vec3 predicted = targetPos.add(velocity.scale(leadStrength));

        Vec3 fromSelf = predicted.subtract(entity.position()).normalize();

        return predicted.add(0, diveHeight, 0);
    }
}