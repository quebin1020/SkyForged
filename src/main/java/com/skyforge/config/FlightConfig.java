package com.skyforge.config;

/**
 * Configuración de vuelo. Usar FlightConfig.builder() o los presets estáticos.
 *
 * Presets listos para usar:
 *   FlightConfig.HELICOPTER  — VTOL compacto
 *   FlightConfig.GUNSHIP     — VTOL pesado
 *   FlightConfig.PLANE       — ala fija rápida
 *   FlightConfig.SCOUT       — caza ágil
 *   FlightConfig.AIRSHIP     — dirigible lento/estable
 *
 * Personalización inline:
 *   FlightConfig.builder().maxSpeed(0.5f).turnRate(6f).build()
 */
public class FlightConfig {

    public float maxSpeed;
    public float acceleration;
    public float turnRate;
    public float climbRate;
    public float drag;
    public boolean canRotateInPlace;
    public boolean requiresForwardMovement;
    public float minTerrainClearance;

    // Multiplicador de velocidad temporal (usado por boss phases)
    public float speedMultiplier = 1.0f;

    public FlightConfig(
            float maxSpeed,
            float acceleration,
            float turnRate,
            float climbRate,
            float drag,
            boolean canRotateInPlace,
            boolean requiresForwardMovement,
            float minTerrainClearance
    ) {
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.turnRate = turnRate;
        this.climbRate = climbRate;
        this.drag = drag;
        this.canRotateInPlace = canRotateInPlace;
        this.requiresForwardMovement = requiresForwardMovement;
        this.minTerrainClearance = minTerrainClearance;
    }

    /** Compat con el constructor anterior (sin minTerrainClearance). */
    public FlightConfig(
            float maxSpeed, float acceleration, float turnRate, float climbRate,
            float drag, boolean canRotateInPlace, boolean requiresForwardMovement
    ) {
        this(maxSpeed, acceleration, turnRate, climbRate, drag,
                canRotateInPlace, requiresForwardMovement, 15f);
    }

    /** Velocidad efectiva considerando el multiplicador de fase. */
    public float effectiveMaxSpeed() {
        return maxSpeed * speedMultiplier;
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    public static final FlightConfig HELICOPTER = builder()
            .maxSpeed(0.35f).acceleration(0.03f).turnRate(4f).drag(0.98f)
            .canRotateInPlace(true).build();

    public static final FlightConfig GUNSHIP = builder()
            .maxSpeed(0.42f).acceleration(0.035f).turnRate(5f).drag(0.978f)
            .canRotateInPlace(true).build();

    public static final FlightConfig PLANE = builder()
            .maxSpeed(0.65f).acceleration(0.06f).turnRate(2.5f).drag(0.92f)
            .requiresForwardMovement(true).minTerrainClearance(22f).build();

    public static final FlightConfig SCOUT = builder()
            .maxSpeed(0.95f).acceleration(0.09f).turnRate(9f).drag(0.93f)
            .requiresForwardMovement(true).minTerrainClearance(9f).build();

    public static final FlightConfig AIRSHIP = builder()
            .maxSpeed(0.12f).acceleration(0.01f).turnRate(10f).drag(0.995f)
            .canRotateInPlace(false).build();

    // ── Builder ───────────────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private float maxSpeed = 0.35f;
        private float acceleration = 0.03f;
        private float turnRate = 4f;
        private float climbRate = 0.02f;
        private float drag = 0.98f;
        private boolean canRotateInPlace = true;
        private boolean requiresForwardMovement = false;
        private float minTerrainClearance = 15f;

        public Builder maxSpeed(float v)              { maxSpeed = v;              return this; }
        public Builder acceleration(float v)          { acceleration = v;          return this; }
        public Builder turnRate(float v)              { turnRate = v;              return this; }
        public Builder climbRate(float v)             { climbRate = v;             return this; }
        public Builder drag(float v)                  { drag = v;                  return this; }
        public Builder canRotateInPlace(boolean v)    { canRotateInPlace = v;      return this; }
        public Builder requiresForwardMovement(boolean v) { requiresForwardMovement = v; return this; }
        public Builder minTerrainClearance(float v)   { minTerrainClearance = v;   return this; }

        public FlightConfig build() {
            return new FlightConfig(maxSpeed, acceleration, turnRate, climbRate,
                    drag, canRotateInPlace, requiresForwardMovement, minTerrainClearance);
        }
    }
}
