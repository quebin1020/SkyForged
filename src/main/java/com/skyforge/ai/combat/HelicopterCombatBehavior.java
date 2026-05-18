package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HelicopterCombatBehavior
        implements CombatBehavior {

    protected final AbstractAerialEntity entity;

    /*
        DISTANCIA ORBITAL
     */

    protected double orbitRadius = 45;

    /*
        VELOCIDAD ORBITAL
     */

    protected double orbitSpeed = 0.008;

    /*
        ALTURA SOBRE OBJETIVO
     */

    protected double preferredHeight = 18;

    /*
        ÁNGULO ACTUAL
     */

    protected double orbitAngle;

    /*
        OFFSET INDIVIDUAL
     */

    protected final double orbitOffset;

    public HelicopterCombatBehavior(
            AbstractAerialEntity entity
    ) {

        this.entity = entity;

        /*
            OFFSET ÚNICO
         */

        this.orbitOffset =
                entity.getRandom()
                        .nextDouble()
                        * Math.PI * 2;

        /*
            COMENZAR EN OFFSET
         */

        this.orbitAngle =
                orbitOffset;
    }

    @Override
    public Vec3 getAttackPosition(
            LivingEntity target
    ) {

        /*
            AVANZAR ÓRBITA
         */

        orbitAngle += orbitSpeed;

        /*
            POSICIÓN ORBITAL
         */

        double offsetX =
                Math.cos(orbitAngle)
                        * orbitRadius;

        double offsetZ =
                Math.sin(orbitAngle)
                        * orbitRadius;

        /*
            POSICIÓN FINAL
         */

        return target.position().add(

                offsetX,

                preferredHeight,

                offsetZ
        );
    }
}