package com.skyforge.movement;

import com.skyforge.config.FlightConfig;
import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * Movimiento para aviones de ala fija.
 *
 * Física:
 *  - Thrust siempre hacia adelante (getLookAngle).
 *  - Yaw gira hacia el target a FlightConfig.turnRate grados/tick.
 *  - Velocidad mínima horizontal (60% maxSpeed) — no puede detenerse.
 *  - Altitude error → corrección vertical suave.
 *
 * Terrain avoidance (dos capas):
 *  LOOKAHEAD — muestrea el terreno 18 bloques adelante. Si el suelo sube
 *              antes de que el avión pueda esquivarlo, empieza a subir.
 *  CLEARANCE — si la distancia al suelo es menor que minTerrainClearance,
 *              aplica pull-up proporcional a la urgencia. Cuanto más cerca
 *              del suelo, más fuerte el pull-up. Anula cualquier fuerza
 *              descendente del control de altitud normal.
 */
public class AirplaneMovement extends FlightMovementController {

    private static final double LOOKAHEAD_DIST    = 18.0; // bloques adelante a comprobar
    private static final double LOOKAHEAD_FACTOR  = 1.6;  // clearance × factor = umbral lookahead

    public AirplaneMovement(AbstractAerialEntity entity, FlightConfig config) {
        super(entity, config);
    }

    @Override
    protected void applyMovement() {
        if (targetPosition == null) return;

        Vec3 current  = entity.getDeltaMovement();
        Vec3 pos      = entity.position();
        Vec3 toTarget = targetPosition.subtract(pos);

        // ── Yaw: girar hacia target a tasa máxima ──
        Vec3 flatToTarget = new Vec3(toTarget.x, 0, toTarget.z);
        if (flatToTarget.lengthSqr() > 0.001) {
            float targetYaw = (float)(Math.atan2(flatToTarget.z, flatToTarget.x) * (180.0 / Math.PI)) - 90f;
            entity.setYRot(rotateTowards(entity.getYRot(), targetYaw, config.turnRate));
        }

        // ── Thrust hacia adelante ──
        Vec3 forward = entity.getLookAngle().normalize();
        Vec3 thrust  = forward.scale(config.maxSpeed).subtract(current).scale(config.acceleration);

        // ── Control de altitud normal ──
        double desiredVY     = Mth.clamp(toTarget.y * 0.018, -0.18, 0.18);
        double verticalDelta = (desiredVY - current.y) * 0.07;

        // ── Terrain avoidance ──
        verticalDelta = applyTerrainAvoidance(pos, current, forward, verticalDelta);

        Vec3 newVel = new Vec3(
                current.x + thrust.x,
                current.y + verticalDelta,
                current.z + thrust.z
        );

        // ── Velocidad mínima horizontal ──
        double minSpeed = config.maxSpeed * 0.6;
        double hSpeed   = Math.sqrt(newVel.x * newVel.x + newVel.z * newVel.z);
        if (hSpeed < minSpeed && hSpeed > 0.0001) {
            double scale = minSpeed / hSpeed;
            newVel = new Vec3(newVel.x * scale, newVel.y, newVel.z * scale);
        }

        entity.setDeltaMovement(newVel);
    }

    /**
     * Calcula el delta vertical de evasión de terreno.
     * Retorna el verticalDelta corregido (nunca menor que el pull-up necesario).
     */
    private double applyTerrainAvoidance(Vec3 pos, Vec3 current, Vec3 forward, double verticalDelta) {
        double clearance    = config.minTerrainClearance;
        double groundY      = getGroundY(pos);
        double currentClear = pos.y - groundY;

        // ── Capa 1: CLEARANCE — suelo justo debajo ──
        if (currentClear < clearance) {
            double urgency  = Mth.clamp(1.0 - (currentClear / clearance), 0.0, 1.0);
            double pullUp   = urgency * 0.35; // fuerza máxima 0.35 bloques/tick²
            verticalDelta   = Math.max(verticalDelta, pullUp);
        }

        // ── Capa 2: LOOKAHEAD — suelo sube adelante ──
        Vec3 lookaheadPos  = pos.add(forward.scale(LOOKAHEAD_DIST));
        double lookaheadGY = getGroundY(lookaheadPos);
        double lookaheadCl = pos.y - lookaheadGY; // clearance en el punto futuro

        if (lookaheadCl < clearance * LOOKAHEAD_FACTOR) {
            double urgency    = Mth.clamp(1.0 - (lookaheadCl / (clearance * LOOKAHEAD_FACTOR)), 0.0, 1.0);
            double preemptive = urgency * 0.20;
            verticalDelta     = Math.max(verticalDelta, preemptive);
        }

        return verticalDelta;
    }

    private double getGroundY(Vec3 pos) {
        return entity.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) pos.x, (int) pos.z);
    }

    protected float rotateTowards(float current, float target, float maxDelta) {
        float delta = Mth.wrapDegrees(target - current);
        delta = Mth.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }
}
