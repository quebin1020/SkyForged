package com.skyforge.ai.combat;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface CombatPlatform {

    /*
        POSICIÓN / MOVIMIENTO
     */

    Vec3 getCombatPosition();

    Vec3 getCombatVelocity();

    /*
        AIM
     */

    Vec3 getTurretOrigin(int id);


    /*
        TARGET
     */

    LivingEntity getCombatTarget();

    /*
        WORLD
     */

    Level getCombatLevel();

    RandomSource getCombatRandom();

    /*
        ENTITY OWNER
     */

    Entity getCombatOwner();

    /*
        SPAWN
     */

    void spawnCombatEntity(
            Entity entity
    );

    /*
        CONTROLLERS
     */

    AimController getAimController(int id);

    int getTurretCount();
}