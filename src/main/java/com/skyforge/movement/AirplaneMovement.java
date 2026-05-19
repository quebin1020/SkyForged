package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AirplaneMovement extends FlightMovementController {

    public AirplaneMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        if (targetPosition == null)
            return;

        Vec3 current = entity.getDeltaMovement();
        Vec3 pos = entity.position();

        Vec3 toTarget = targetPosition.subtract(pos);

        // ===== FORWARD =====
        Vec3 forward = entity.getLookAngle();

        Vec3 desiredForward = forward.normalize()
                .scale(config.maxSpeed);

        Vec3 forwardSteering = desiredForward
                .subtract(current)
                .scale(config.acceleration);

        // ===== LIFT (CLAVE QUE TE FALTABA) =====
        double verticalError = toTarget.y;

        double lift = Mth.clamp(verticalError * 0.015, -0.2, 0.2);

        double verticalSteering = (lift - current.y) * 0.08;

        // ===== FINAL =====
        Vec3 finalVelocity = new Vec3(
                current.x + forwardSteering.x,
                current.y + verticalSteering,
                current.z + forwardSteering.z
        );

        entity.setDeltaMovement(finalVelocity);

        // rotate only if moving
        Vec3 flat = new Vec3(finalVelocity.x, 0, finalVelocity.z);

        if (flat.lengthSqr() > 0.001) {
            entity.setYRot(
                    (float)(Math.atan2(flat.z, flat.x) * (180 / Math.PI)) - 90f
            );
        }
    }
}
