package com.skyforge.attack;

import com.skyforge.entity.AbstractAerialEntity;

public class MachineGunAttack extends AttackController {

    public MachineGunAttack(AbstractAerialEntity entity) {
        super(entity);
    }

    @Override
    protected void attackTick() {

        if (cooldown > 0)
            return;

        // disparar proyectil

        cooldown = 10;
    }
}
