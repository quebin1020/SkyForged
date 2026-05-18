package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HelicopterMovement
        extends FlightMovementController {

    protected double verticalStrength = 0.15;

    protected double maxVerticalSpeed = 0.8;

    protected double verticalAcceleration = 0.2;

    public HelicopterMovement(
            AbstractAerialEntity entity,
            FlightConfig config
    ) {

        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        if(targetPosition == null)
            return;

        Vec3 toTarget =
                targetPosition.subtract(
                        entity.position()
                );

        double distance =
                toTarget.length();

        /*
            DEBUG
         */

        System.out.println(
                "TARGET Y: " + targetPosition.y
        );

        System.out.println(
                "ENTITY Y: " + entity.position().y
        );

        System.out.println(
                "DIFF Y: " + toTarget.y
        );

        System.out.println(
                "CURRENT VELOCITY: "
                        + entity.getDeltaMovement()
        );

        /*
            SI LLEGÓ
         */

        if(distance < 2) {

            entity.setDeltaMovement(

                    entity.getDeltaMovement()
                            .scale(0.8)
            );

            return;
        }

        /*
            MOVIMIENTO HORIZONTAL
         */

        Vec3 horizontalDirection =
                new Vec3(
                        toTarget.x,
                        0,
                        toTarget.z
                );

        if(horizontalDirection.lengthSqr() > 0.0001) {

            horizontalDirection =
                    horizontalDirection.normalize();
        }

        double horizontalDistance =
                Math.sqrt(
                        toTarget.x * toTarget.x
                                +
                                toTarget.z * toTarget.z
                );

        double speedFactor =
                Math.min(
                        horizontalDistance / 20.0,
                        1.0
                );

        double targetSpeed =
                config.maxSpeed
                        * speedFactor;

        Vec3 desiredHorizontalVelocity =
                horizontalDirection.scale(
                        targetSpeed
                );

        /*
            VELOCIDAD ACTUAL
         */

        Vec3 currentVelocity =
                entity.getDeltaMovement();

        /*
            STEERING HORIZONTAL
         */

        Vec3 currentHorizontalVelocity =
                new Vec3(
                        currentVelocity.x,
                        0,
                        currentVelocity.z
                );

        Vec3 horizontalSteering =
                desiredHorizontalVelocity
                        .subtract(
                                currentHorizontalVelocity
                        )
                        .scale(
                                config.acceleration
                        );

        /*
            CONTROL VERTICAL
         */

        double verticalDifference =
                toTarget.y;

        double desiredVerticalVelocity =
                Mth.clamp(

                        verticalDifference
                                * verticalStrength,

                        -maxVerticalSpeed,

                        maxVerticalSpeed
                );

        double verticalSteering =
                (
                        desiredVerticalVelocity
                                - currentVelocity.y
                ) * verticalAcceleration;

        /*
            VELOCIDAD FINAL
         */

        Vec3 finalVelocity =
                new Vec3(

                        currentVelocity.x
                                + horizontalSteering.x,

                        currentVelocity.y
                                + verticalSteering,

                        currentVelocity.z
                                + horizontalSteering.z
                );

        /*
            LIMITAR VELOCIDAD
         */

        double horizontalSpeed =
                Math.sqrt(
                        finalVelocity.x * finalVelocity.x
                                +
                                finalVelocity.z * finalVelocity.z
                );

        if(horizontalSpeed > config.maxSpeed) {

            double scale =
                    config.maxSpeed
                            / horizontalSpeed;

            finalVelocity =
                    new Vec3(

                            finalVelocity.x * scale,

                            finalVelocity.y,

                            finalVelocity.z * scale
                    );
        }

        /*
            APLICAR
         */

        entity.setDeltaMovement(
                finalVelocity
        );
    }
}