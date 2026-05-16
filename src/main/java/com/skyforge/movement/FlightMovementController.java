package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public abstract class FlightMovementController {

    protected final AbstractAerialEntity entity;
    protected final FlightConfig config;

    protected Vec3 targetPosition;

    public FlightMovementController(AbstractAerialEntity entity, FlightConfig config) {
        this.entity = entity;
        this.config = config;
    }

    public void tick() {

        if (targetPosition == null) return;
        rotateTowardTarget();
        applyMovement();
        applyDrag();
    }

    protected void rotateTowardTarget() {

        Vec3 direction = targetPosition.subtract(entity.position());

        double targetYaw = Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90;

        float currentYaw = entity.getYRot();

        float delta = Mth.wrapDegrees((float) targetYaw - currentYaw);

        delta = Mth.clamp(delta, -config.turnRate, config.turnRate);

        entity.setYRot(currentYaw + delta);
    }

    protected void applyMovement() {

        Vec3 toTarget = targetPosition.subtract(entity.position()).normalize();

        Vec3 forward = entity.getLookAngle().normalize();

        double alignment = forward.dot(toTarget);

        alignment = Math.max(0, alignment);

        Vec3 velocity = entity.getDeltaMovement();

        velocity = velocity.add(forward.scale(config.acceleration * alignment));

        double speed = velocity.length();

        if (speed > config.maxSpeed) {
            velocity = velocity.normalize().scale(config.maxSpeed);
        }

        entity.setDeltaMovement(velocity);
    }

    protected void applyDrag() {

        Vec3 velocity = entity.getDeltaMovement();

        velocity = velocity.scale(config.drag);

        entity.setDeltaMovement(velocity);
    }

    public void setTargetPosition(Vec3 targetPosition) {
        this.targetPosition = targetPosition;
    }
}
