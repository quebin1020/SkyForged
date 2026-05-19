package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AirshipMovement extends FlightMovementController {

    public AirshipMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        if (targetPosition == null)
            return;

        Vec3 current = entity.getDeltaMovement();
        Vec3 pos = entity.position();

        Vec3 toTarget = targetPosition.subtract(pos);

        // ===== HORIZONTAL =====
        Vec3 horizontal = new Vec3(toTarget.x, 0, toTarget.z);

        double horizontalDist = horizontal.length();

        Vec3 desiredHorizontal = Vec3.ZERO;

        if (horizontalDist > 0.0001) {
            desiredHorizontal = horizontal.normalize()
                    .scale(config.maxSpeed * 0.35);
        }

        Vec3 currentHorizontal = new Vec3(current.x, 0, current.z);

        Vec3 horizontalSteering = desiredHorizontal
                .subtract(currentHorizontal)
                .scale(config.acceleration * 0.25);

        // ===== VERTICAL (CLAVE FIX) =====
        double verticalError = toTarget.y;

        double desiredYVelocity =
                Mth.clamp(verticalError * 0.02, -0.15, 0.15);

        double verticalSteering =
                (desiredYVelocity - current.y) * 0.05;

        // ===== FINAL =====
        Vec3 finalVelocity = new Vec3(
                current.x + horizontalSteering.x,
                current.y + verticalSteering,
                current.z + horizontalSteering.z
        );

        // limit horizontal
        double hSpeed = Math.sqrt(finalVelocity.x * finalVelocity.x + finalVelocity.z * finalVelocity.z);

        if (hSpeed > config.maxSpeed * 0.4) {
            double s = (config.maxSpeed * 0.4) / hSpeed;
            finalVelocity = new Vec3(
                    finalVelocity.x * s,
                    finalVelocity.y,
                    finalVelocity.z * s
            );
        }

        entity.setDeltaMovement(finalVelocity);
    }
}
