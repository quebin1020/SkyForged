package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;

public class AirplaneMovement extends FlightMovementController {

    public AirplaneMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {

        // IMPORTANTE:
        // el avión siempre debe avanzar
        // incluso girando

        super.applyMovement();

        // luego:
        // banking
        // overshoot
        // attack runs
    }
}
