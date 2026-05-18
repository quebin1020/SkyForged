package com.skyforge.entity;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.CombatPlatform;
import com.skyforge.attack.AttackController;
import com.skyforge.targeting.TargetingSystem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTurretEntity
        extends Mob
        implements CombatPlatform {

    protected TargetingSystem targeting;

    protected AttackController attackController;

    protected AimController aimController;

    protected AbstractTurretEntity(
            EntityType<? extends Mob> type,
            Level level
    ) {

        super(type, level);

        this.aimController =
                new AimController(this);
    }

    @Override
    public void tick() {

        super.tick();

        if(level().isClientSide())
            return;

        if(targeting != null)
            targeting.tick();

        if(aimController != null)
            aimController.tick();

        if(attackController != null)
            attackController.tick();
    }

    @Override
    public Vec3 getCombatPosition() {

        return this.position();
    }

    @Override
    public Vec3 getCombatVelocity() {

        return Vec3.ZERO;
    }

    @Override
    public Vec3 getAimOrigin() {

        return this.position().add(
                0,
                1.5,
                0
        );
    }

    @Override
    public Level getCombatLevel() {

        return this.level();
    }

    @Override
    public RandomSource getCombatRandom() {

        return this.getRandom();
    }

    @Override
    public LivingEntity getCombatTarget() {

        if(targeting == null)
            return null;

        return targeting.getTarget();
    }

    @Override
    public Entity getCombatOwner() {

        return this;
    }

    @Override
    public void spawnCombatEntity(
            Entity entity
    ) {

        this.level()
                .addFreshEntity(entity);
    }

    @Override
    public AimController getAimController() {

        return aimController;
    }

    public TargetingSystem getTargetingSystem() {

        return targeting;
    }

    public AttackController getAttackController() {

        return attackController;
    }
}