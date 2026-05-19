package com.skyforge.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AimController {

    public enum AimMode {
        FREE_TRACKING,   // helicóptero
        LIMITED_TURN,    // airship
        FIXED_GUN        // avión
    }

    protected final AimProfile profile;
    protected final CombatPlatform platform;
    protected final int turretId;

    protected Vec3 aimedPosition;
    protected Vec3 predictedPosition;

    protected AimMode mode = AimMode.FREE_TRACKING;

    protected double aimSpeed = 0.15;
    protected double projectileSpeed = 2;

    protected double firingTolerance = 8;
    protected double baseInaccuracy = 0.05;

    protected boolean predictiveAim = true;

    public AimController(CombatPlatform platform, int id, AimProfile profile) {
        this.platform = platform;
        this.turretId = id;
        this.profile = profile;
    }

    public void tick(LivingEntity target) {

        if (target == null) {
            aimedPosition = null;
            predictedPosition = null;
            return;
        }

        updatePrediction(target);
        updateAim();
    }

    protected void updatePrediction(LivingEntity target) {

        Vec3 targetPos = target.position();

        if(!profile.predictive) {
            predictedPosition = platform.getCombatTarget().position();
            return;
        }
        double distance = platform.getCombatPosition().distanceTo(targetPos);
        double travelTime = distance / profile.projectileSpeed;
        predictedPosition = targetPos.add(
                target.getDeltaMovement().scale(travelTime)
        );
    }

    protected void updateAim() {

        if (predictedPosition == null)
            return;

        if (aimedPosition == null) {
            aimedPosition = predictedPosition;
            return;
        }

        Vec3 delta = predictedPosition.subtract(aimedPosition);

        switch (mode) {

            case FREE_TRACKING -> {
                aimedPosition = aimedPosition.add(delta.scale(profile.aimSpeed));
            }

            case LIMITED_TURN -> {
                aimedPosition = aimedPosition.add(delta.scale(profile.aimSpeed * 0.4));
            }

            case FIXED_GUN -> {
                // no “tracking libre”, solo lock si ya está alineado
                if (!isAligned()) return;
                aimedPosition = predictedPosition;
            }
        }
    }

    private double getToleranceMultiplier() {
        return switch (mode) {
            case FREE_TRACKING -> 1.0;
            case LIMITED_TURN -> 0.6;
            case FIXED_GUN -> 0.25;
        };
    }

    public Vec3 getAimDirection() {

        if (aimedPosition == null)
            return Vec3.ZERO;

        Vec3 origin = platform.getTurretOrigin(turretId);
        return aimedPosition.subtract(origin).normalize();
    }

    public boolean canShoot() {

        if (predictedPosition == null)
            return false;

        Vec3 origin = platform.getTurretOrigin(turretId);

        Vec3 current = getAimDirection();
        Vec3 desired = predictedPosition.subtract(origin).normalize();

        double dot = current.dot(desired);
        dot = Math.max(-1, Math.min(1, dot));

        double angle = Math.toDegrees(Math.acos(dot));

        double tolerance = firingTolerance * getToleranceMultiplier();

        return angle <= tolerance;
    }

    private boolean isAligned() {

        if (predictedPosition == null)
            return false;

        Vec3 origin = platform.getTurretOrigin(turretId);

        Vec3 current = getAimDirection();
        Vec3 desired = predictedPosition.subtract(origin).normalize();

        double dot = current.dot(desired);
        dot = Math.max(-1, Math.min(1, dot));

        double angle = Math.toDegrees(Math.acos(dot));

        return angle <= firingTolerance * 0.25;
    }

    public Vec3 applyInaccuracy(Vec3 direction) {

        direction = direction.add(
                (platform.getCombatRandom().nextDouble() - 0.5) * profile.baseInaccuracy,
                (platform.getCombatRandom().nextDouble() - 0.5) * profile.baseInaccuracy,
                (platform.getCombatRandom().nextDouble() - 0.5) * profile.baseInaccuracy
        );

        return direction.normalize();
    }

    // ================= CONFIG =================

    public void setMode(AimMode mode) {
        this.mode = mode;
    }

    public void setAimSpeed(double aimSpeed) {
        this.aimSpeed = aimSpeed;
    }

    public void setProjectileSpeed(double projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
    }

    public void setFiringTolerance(double firingTolerance) {
        this.firingTolerance = firingTolerance;
    }

    public void setBaseInaccuracy(double baseInaccuracy) {
        this.baseInaccuracy = baseInaccuracy;
    }

    public Vec3 getAimedPosition() {
        return aimedPosition;
    }

    public Vec3 getPredictedPosition() {
        return predictedPosition;
    }
}