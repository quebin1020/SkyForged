package com.skyforge.config;

public class FlightConfig {

    public float maxSpeed;
    public float acceleration;
    public float turnRate;
    public float climbRate;
    public float drag;

    public boolean canRotateInPlace;
    public boolean requiresForwardMovement;

    // Distancia mínima al suelo antes de hacer pull-up automático
    // Solo relevante para AirplaneMovement. Default 15 bloques.
    public float minTerrainClearance = 15f;

    public FlightConfig(
            float maxSpeed,
            float acceleration,
            float turnRate,
            float climbRate,
            float drag,
            boolean canRotateInPlace,
            boolean requiresForwardMovement
    ) {
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.turnRate = turnRate;
        this.climbRate = climbRate;
        this.drag = drag;
        this.canRotateInPlace = canRotateInPlace;
        this.requiresForwardMovement = requiresForwardMovement;
    }
}