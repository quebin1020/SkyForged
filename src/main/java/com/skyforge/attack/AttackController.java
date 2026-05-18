package com.skyforge.attack;

import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;

public abstract class AttackController {

    protected final CombatPlatform platform;
    protected int cooldown;

    public AttackController(
            CombatPlatform platform
    ) {

        this.platform = platform;
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

        return platform.getCombatTarget();
    }

    protected abstract void attackTick();
}