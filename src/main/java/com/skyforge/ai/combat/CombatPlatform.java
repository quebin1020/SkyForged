package com.skyforge.ai.combat;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface CombatPlatform {

    Vec3 getCombatPosition();

    Vec3 getCombatVelocity();

    Vec3 getAimOrigin();

    Level getCombatLevel();

    RandomSource getCombatRandom();

    LivingEntity getCombatTarget();

    Entity getCombatOwner();

    void spawnCombatEntity(Entity entity);

    AimController getAimController();
}