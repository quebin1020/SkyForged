package com.skyforge.entity;

import com.skyforge.attack.TurretAttackController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

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

        this.aimController
                .setAimSpeed(0.08);

        this.aimController
                .setProjectileSpeed(3);

        this.aimController
                .setFiringTolerance(6);

        this.aimController
                .setBaseInaccuracy(0.03);
    }
}