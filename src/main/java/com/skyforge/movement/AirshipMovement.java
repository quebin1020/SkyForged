package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;

public class AirshipMovement extends FlightMovementController {

    public AirshipMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        // pesado
        // lento
        // mucha inercia

        super.applyMovement();
    }
}
