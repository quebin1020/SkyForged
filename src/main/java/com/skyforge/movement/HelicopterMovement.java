package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;

public class HelicopterMovement extends FlightMovementController {

    public HelicopterMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        super.applyMovement();

        // después:
        // hover
        // orbit
        // strafing
    }
}
