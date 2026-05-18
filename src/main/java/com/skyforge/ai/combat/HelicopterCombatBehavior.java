package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HelicopterCombatBehavior implements CombatBehavior {
    protected final AbstractAerialEntity entity;

    protected double orbitRadius = 40;

    protected double orbitSpeed = 0.01;

    protected double preferredHeight = 10;
    public HelicopterCombatBehavior(
            AbstractAerialEntity entity
    ) {

        this.entity = entity;
    }
    @Override
    public Vec3 getAttackPosition(
            LivingEntity target
    ) {

        double angle = entity.tickCount * orbitSpeed;

        double offsetX = Math.cos(angle) * orbitRadius;

        double offsetZ = Math.sin(angle) * orbitRadius;

        return target.position().add(
                offsetX,
                preferredHeight,
                offsetZ
        );
    }
}