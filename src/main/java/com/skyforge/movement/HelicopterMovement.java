package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HelicopterMovement
        extends FlightMovementController {

    /*
        CONTROL VERTICAL
     */

    protected double verticalStrength = 0.04;

    protected double maxVerticalSpeed = 0.25;

    protected double verticalAcceleration = 0.08;

    protected double verticalResponse = 0.04;

    /*
        HOVER
     */

    protected double hoverDrag = 0.92;

    public HelicopterMovement(
            AbstractAerialEntity entity,
            FlightConfig config
    ) {

        super(entity, config);
    }
    protected float rotateTowards(
            float current,
            float target,
            float maxTurn
    ) {

        float delta =
                Mth.wrapDegrees(
                        target - current
                );

        delta =
                Mth.clamp(
                        delta,
                        -maxTurn,
                        maxTurn
                );

        return current + delta;
    }

    @Override
    protected void applyMovement() {


        if(targetPosition == null)
            return;

        Vec3 currentPosition =
                entity.position();

        Vec3 currentVelocity =
                entity.getDeltaMovement();

        Vec3 toTarget =
                targetPosition.subtract(
                        currentPosition
                );

        double distance =
                toTarget.length();

        /*
            DEBUG
         */
        /*
        System.out.println(
                "TARGET: " + targetPosition
        );

        System.out.println(
                "POSITION: " + currentPosition
        );

        System.out.println(
                "TO TARGET: " + toTarget
        );

        System.out.println(
                "VELOCITY: " + currentVelocity
        );*/

        /*
            LLEGÓ
         */

        if(distance < 2) {

            entity.setDeltaMovement(

                    currentVelocity.scale(
                            hoverDrag
                    )
            );

            return;
        }

        /*
            DIRECCIÓN HORIZONTAL
         */

        Vec3 horizontalOffset =
                new Vec3(

                        toTarget.x,

                        0,

                        toTarget.z
                );

        double horizontalDistance =
                horizontalOffset.length();

        Vec3 horizontalDirection =
                Vec3.ZERO;

        if(horizontalDistance > 0.0001) {

            horizontalDirection =
                    horizontalOffset.normalize();
        }

        /*
            VELOCIDAD OBJETIVO
         */

        double speedFactor =
                Math.min(
                        horizontalDistance / 20.0,
                        1.0
                );

        double targetSpeed =
                config.effectiveMaxSpeed()
                        * speedFactor;

        Vec3 desiredHorizontalVelocity =
                horizontalDirection.scale(
                        targetSpeed
                );

        /*
            VELOCIDAD HORIZONTAL ACTUAL
         */

        Vec3 currentHorizontalVelocity =
                new Vec3(

                        currentVelocity.x,

                        0,

                        currentVelocity.z
                );

        /*
            STEERING HORIZONTAL
         */

        Vec3 horizontalSteering =
                desiredHorizontalVelocity
                        .subtract(
                                currentHorizontalVelocity
                        )
                        .scale(
                                config.acceleration
                        );

        /*
            CONTROL VERTICAL SUAVIZADO
         */

        double verticalDifference =
                targetPosition.y - currentPosition.y;

        double targetY = targetPosition.y;
        double currentY = currentPosition.y;

        double verticalError = targetY - currentY;

        double desiredVerticalVelocity =
                Mth.clamp(verticalError * verticalStrength,
                        -maxVerticalSpeed,
                        maxVerticalSpeed);

        double verticalSteering =
                (desiredVerticalVelocity - currentVelocity.y)
                        * verticalAcceleration;

        /*
            SUAVIZAR RESPUESTA
         */

        double verticalDelta =
                desiredVerticalVelocity
                        - currentVelocity.y;

        verticalDelta =
                Mth.clamp(

                        verticalDelta,

                        -verticalResponse,

                        verticalResponse
                );



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
            LIMITADOR HORIZONTAL
         */

        double horizontalSpeed =
                Math.sqrt(

                        finalVelocity.x * finalVelocity.x
                                +
                                finalVelocity.z * finalVelocity.z
                );

        if(horizontalSpeed > config.effectiveMaxSpeed()) {

            double scale =
                    config.effectiveMaxSpeed()
                            / horizontalSpeed;

            finalVelocity =
                    new Vec3(

                            finalVelocity.x * scale,

                            finalVelocity.y,

                            finalVelocity.z * scale
                    );
        }

        /*
            LIMITADOR VERTICAL
         */

        double clampedVertical =
                Mth.clamp(

                        finalVelocity.y,

                        -maxVerticalSpeed,

                        maxVerticalSpeed
                );

        finalVelocity =
                new Vec3(

                        finalVelocity.x,

                        clampedVertical,

                        finalVelocity.z
                );

        if(horizontalDistance > 0.001) {

            float targetYaw =
                    (float)(
                            Math.atan2(
                                    finalVelocity.z,
                                    finalVelocity.x
                            ) * (180F / Math.PI)
                    ) - 90f;

            entity.setYRot(
                    rotateTowards(

                            entity.getYRot(),

                            targetYaw,

                            4f
                    )
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