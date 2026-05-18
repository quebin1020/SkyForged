package com.skyforge.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AimController {

    protected final CombatPlatform platform;

    protected Vec3 aimedPosition;

    protected Vec3 predictedPosition;

    protected double aimSpeed = 0.15;

    protected double projectileSpeed = 2;

    protected double firingTolerance = 8;

    protected double baseInaccuracy = 0.05;

    protected boolean predictiveAim = true;

    protected boolean rotatingAim = true;

    public AimController(
            CombatPlatform platform
    ) {

        this.platform = platform;
    }

    public void tick() {

        LivingEntity target =
                platform.getCombatTarget();

        if(target == null) {

            aimedPosition = null;

            predictedPosition = null;

            return;
        }

        updatePrediction(target);

        updateAim();
    }

    protected void updatePrediction(
            LivingEntity target
    ) {

        Vec3 targetPosition =
                target.position();

        if(!predictiveAim) {

            predictedPosition =
                    targetPosition;

            return;
        }

        double distance =
                platform.getCombatPosition()
                        .distanceTo(
                                targetPosition
                        );

        double travelTime =
                distance / projectileSpeed;

        predictedPosition =
                targetPosition.add(
                        target.getDeltaMovement()
                                .scale(travelTime)
                );
    }

    protected void updateAim() {

        if(predictedPosition == null)
            return;

        if(aimedPosition == null) {

            aimedPosition =
                    predictedPosition;

            return;
        }

        Vec3 delta =
                predictedPosition.subtract(
                        aimedPosition
                );

        aimedPosition =
                aimedPosition.add(
                        delta.scale(aimSpeed)
                );
    }

    public Vec3 getAimDirection() {

        if(aimedPosition == null)
            return Vec3.ZERO;

        return aimedPosition
                .subtract(
                        platform.getAimOrigin()
                )
                .normalize();
    }

    public boolean canShoot() {

        LivingEntity target =
                platform.getCombatTarget();

        if(target == null)
            return false;

        if(predictedPosition == null)
            return false;

        Vec3 currentAim =
                getAimDirection();

        Vec3 desiredAim =
                predictedPosition
                        .subtract(
                                platform.getAimOrigin()
                        )
                        .normalize();

        double dot =
                currentAim.dot(
                        desiredAim
                );

        dot = Math.max(
                -1,
                Math.min(1, dot)
        );

        double angle =
                Math.toDegrees(
                        Math.acos(dot)
                );

        return angle <= firingTolerance;
    }

    public Vec3 applyInaccuracy(
            Vec3 direction
    ) {

        direction = direction.add(

                (
                        platform.getCombatRandom()
                                .nextDouble() - 0.5
                ) * baseInaccuracy,

                (
                        platform.getCombatRandom()
                                .nextDouble() - 0.5
                ) * baseInaccuracy,

                (
                        platform.getCombatRandom()
                                .nextDouble() - 0.5
                ) * baseInaccuracy
        );

        return direction.normalize();
    }

    public Vec3 getAimedPosition() {

        return aimedPosition;
    }

    public Vec3 getPredictedPosition() {

        return predictedPosition;
    }

    public void setAimSpeed(
            double aimSpeed
    ) {

        this.aimSpeed = aimSpeed;
    }

    public void setProjectileSpeed(
            double projectileSpeed
    ) {

        this.projectileSpeed =
                projectileSpeed;
    }

    public void setFiringTolerance(
            double firingTolerance
    ) {

        this.firingTolerance =
                firingTolerance;
    }

    public void setBaseInaccuracy(
            double baseInaccuracy
    ) {

        this.baseInaccuracy =
                baseInaccuracy;
    }

    public void setPredictiveAim(
            boolean predictiveAim
    ) {

        this.predictiveAim =
                predictiveAim;
    }

    public void setRotatingAim(
            boolean rotatingAim
    ) {

        this.rotatingAim =
                rotatingAim;
    }
}