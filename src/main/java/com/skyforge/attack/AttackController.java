package com.skyforge.attack;

import com.skyforge.entity.AbstractAerialEntity;

public abstract class AttackController {

    protected final AbstractAerialEntity entity;

    protected int cooldown;

    public AttackController(AbstractAerialEntity entity) {
        this.entity = entity;
    }

    public void tick() {

        if (cooldown > 0)
            cooldown--;

        attackTick();
    }

    protected abstract void attackTick();
}
