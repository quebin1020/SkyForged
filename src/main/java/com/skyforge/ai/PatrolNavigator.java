package com.skyforge.ai;

import com.skyforge.config.PatrolConfig;
import com.skyforge.entity.AbstractAerialEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class PatrolNavigator {

    protected final AbstractAerialEntity entity;

    protected final PatrolConfig patrolConfig;

    public PatrolNavigator(
            AbstractAerialEntity entity,
            PatrolConfig patrolConfig
    ) {

        this.entity = entity;

        this.patrolConfig = patrolConfig;
    }

    protected Vec3 generatePatrolPoint() {

        RandomSource random =
                entity.getRandom();

        /*
            DISTANCIA
         */

        double distance =
                Mth.lerp(

                        random.nextDouble(),

                        patrolConfig.minDistance,

                        patrolConfig.maxDistance
                );

        /*
            DIRECCIÓN BASADA SOLO EN YAW
         */

        float yawDegrees =
                entity.getYRot();

        float yawRadians =
                (float)Math.toRadians(
                        yawDegrees
                );

        Vec3 forward =
                new Vec3(

                        -Math.sin(yawRadians),

                        0,

                        Math.cos(yawRadians)
                ).normalize();

        /*
            OFFSET ANGULAR
         */

        float angleOffset;

        if(random.nextDouble()
                < patrolConfig.forwardBias) {

            angleOffset =
                    (random.nextFloat() * 2f - 1f)
                            * patrolConfig.maxAngleOffset;

        } else {

            angleOffset =
                    random.nextFloat() * 360f;

            if(!patrolConfig.allowBehind) {

                angleOffset =
                        Mth.clamp(

                                angleOffset,

                                -patrolConfig.maxAngleOffset,

                                patrolConfig.maxAngleOffset
                        );
            }
        }

        /*
            ROTAR DIRECCIÓN
         */

        Vec3 direction =
                forward.yRot(
                        (float)Math.toRadians(
                                angleOffset
                        )
                );

        /*
            POSICIÓN OBJETIVO
         */

        double targetX =
                entity.position().x
                        + direction.x * distance;

        double targetZ =
                entity.position().z
                        + direction.z * distance;

        double targetY;

        /*
            FOLLOW TERRAIN
         */

        if(patrolConfig.followTerrain) {

            BlockPos samplePos =
                    BlockPos.containing(
                            targetX,
                            entity.position().y,
                            targetZ
                    );

            /*
                IMPORTANTE:
                validar chunk cargado
             */

            if(entity.level()
                    .hasChunkAt(samplePos)) {

                BlockPos groundPos =
                        entity.level()
                                .getHeightmapPos(

                                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,

                                        samplePos
                                );

                targetY =
                        groundPos.getY()
                                + patrolConfig.terrainClearance;

            } else {

                /*
                    fallback seguro
                 */

                targetY =
                        entity.position().y;
            }

        } else {

            /*
                altura relativa
             */

            targetY =
                    entity.position().y
                            + patrolConfig.preferredAltitude;
        }

        /*
            VARIACIÓN VERTICAL
         */

        targetY += (

                random.nextDouble()
                        * patrolConfig.verticalVariance * 2

        ) - patrolConfig.verticalVariance;

        /*
            CLAMPS DE SEGURIDAD
         */

        targetY = Mth.clamp(

                targetY,

                patrolConfig.minimumAltitude,

                patrolConfig.maximumAltitude
        );

        /*
            DEBUG
         */

        System.out.println(
                "PATROL TARGET -> "
                        + targetX
                        + " "
                        + targetY
                        + " "
                        + targetZ
        );

        return new Vec3(

                targetX,

                targetY,

                targetZ
        );
    }
}