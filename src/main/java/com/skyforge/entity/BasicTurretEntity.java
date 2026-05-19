package com.skyforge.entity;

import com.skyforge.ai.combat.AimProfile;
import com.skyforge.attack.TurretAttackController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import com.skyforge.ai.combat.AimController;

public class BasicTurretEntity extends AbstractTurretEntity {

    public BasicTurretEntity(
            EntityType<? extends Mob> type,
            Level level
    ) {

        super(type, level);

        this.targeting =
                new TargetingSystem(this);

        this.attackController =
                new TurretAttackController(this);
        initTurrets();

    }
    protected void initTurrets() {

        AimController t0 = new AimController(this, 0, AimProfile.HELICOPTER);
        AimController t1 = new AimController(this, 1, AimProfile.HELICOPTER);

        t0.setAimSpeed(0.08);
        t0.setProjectileSpeed(3);
        t0.setFiringTolerance(6);
        t0.setBaseInaccuracy(0.03);

        t1.setAimSpeed(0.08);
        t1.setProjectileSpeed(3);
        t1.setFiringTolerance(6);
        t1.setBaseInaccuracy(0.03);

        turrets.put(0, t0);
        turrets.put(1, t1);
    }
}