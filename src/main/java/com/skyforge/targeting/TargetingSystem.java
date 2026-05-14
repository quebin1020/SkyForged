package com.skyforge.targeting;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;

public class TargetingSystem {

    protected final AbstractAerialEntity entity;

    protected LivingEntity currentTarget;

    public TargetingSystem(AbstractAerialEntity entity) {
        this.entity = entity;
    }

    public void tick() {

        // luego:
        // visión
        // memoria
        // prioridad
        // radar
    }

    public LivingEntity getTarget() {
        return currentTarget;
    }
}
