package com.skyforge.attack;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;

public abstract class AttackController {

    protected final AbstractAerialEntity entity;

    protected int cooldown;

    public AttackController(
            AbstractAerialEntity entity
    ) {
        this.entity = entity;
    }

    public void tick() {

        if(cooldown > 0)
            cooldown--;

        attackTick();
    }

    protected boolean canAttack() {

        return cooldown <= 0;
    }

    protected void resetCooldown(int ticks) {

        cooldown = ticks;
    }

    protected LivingEntity getTarget() {

        return entity.getTargetingSystem()
                .getTarget();
    }

    protected abstract void attackTick();
}