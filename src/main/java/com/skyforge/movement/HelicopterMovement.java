package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.phys.Vec3;

public class HelicopterMovement extends FlightMovementController {

    public HelicopterMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        Vec3 toTarget =
                targetPosition.subtract(entity.position());

        double distance = toTarget.length();

        if (distance < 2) {

            entity.setDeltaMovement(
                    entity.getDeltaMovement().scale(0.8)
            );

            return;
        }

        Vec3 desiredDirection =
                toTarget.normalize();

        double speedFactor =
                Math.min(distance / 20.0, 1.0);

        double targetSpeed =
                config.maxSpeed * speedFactor;

        Vec3 desiredVelocity =
                desiredDirection.scale(targetSpeed);

        Vec3 currentVelocity =
                entity.getDeltaMovement();

        Vec3 steering =
                desiredVelocity.subtract(currentVelocity)
                        .scale(config.acceleration);

        entity.setDeltaMovement(
                currentVelocity.add(steering)
        );
    }
}
