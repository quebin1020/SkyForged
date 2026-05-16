package com.skyforge.ai;

import com.skyforge.config.PatrolConfig;
import com.skyforge.config.PatrolPresets;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class PatrolNavigator {
    protected AbstractAerialEntity entity;
    protected PatrolConfig patrolConfig;
    public PatrolNavigator(AbstractAerialEntity entity, PatrolConfig patrolConfig){
        this.entity = entity;
        this.patrolConfig = patrolConfig;
    }
    protected Vec3 generatePatrolPoint() {

        RandomSource random = entity.getRandom();

        double distance = Mth.lerp(
                random.nextDouble(),
                patrolConfig.minDistance,
                patrolConfig.maxDistance
        );

        Vec3 forward = entity.getLookAngle().normalize();

        float angleOffset;

        if(random.nextDouble() < patrolConfig.forwardBias) {

            angleOffset =
                    (random.nextFloat() * 2f - 1f)
                            * patrolConfig.maxAngleOffset;

        } else {

            angleOffset = random.nextFloat() * 360f;

            if(!patrolConfig.allowBehind) {

                angleOffset = Mth.clamp(
                        angleOffset,
                        -patrolConfig.maxAngleOffset,
                        patrolConfig.maxAngleOffset
                );
            }
        }

        Vec3 direction = forward.yRot(
                (float)Math.toRadians(angleOffset)
        );

        double targetX =
                entity.position().x
                        + direction.x * distance;

        double targetZ =
                entity.position().z
                        + direction.z * distance;

        double targetY;

        if(patrolConfig.followTerrain) {

            BlockPos groundPos = entity.level()
                    .getHeightmapPos(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            BlockPos.containing(targetX, 0, targetZ)
                    );

            targetY =
                    groundPos.getY()
                            + patrolConfig.terrainClearance;

        } else {

            targetY = patrolConfig.preferredAltitude;
        }

        targetY += (
                random.nextDouble()
                        * patrolConfig.verticalVariance * 2
        ) - patrolConfig.verticalVariance;

        return new Vec3(
                targetX,
                targetY,
                targetZ
        );
    }
}
