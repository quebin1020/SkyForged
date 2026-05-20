package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Comportamiento de ataque del avión pesado.
 *
 * Ciclo de 3 fases:
 *
 *  APPROACH  — El avión se sitúa en un punto de entrada lejano (160 bloques),
 *              a altura alta (+25). Cuando el target entra en rango (<100),
 *              transiciona a FIRE_RUN.
 *
 *  FIRE_RUN  — El avión vuela directo a un punto de SALIDA al otro lado del
 *              target. Mientras pasa, el PlaneAttackController dispara la
 *              ráfaga. Cuando supera la distancia de corte (>120), entra en BREAK.
 *
 *  BREAK     — Tira hacia arriba y atrás para ganar distancia y altitud.
 *              En cuanto se aleja suficiente, vuelve a APPROACH con un ángulo
 *              diferente (próxima pasada viene de otro lado).
 */
public class PlaneCombatBehavior implements CombatBehavior {

    private enum Phase { APPROACH, FIRE_RUN, BREAK }

    private final AbstractAerialEntity entity;

    private Phase phase = Phase.APPROACH;
    private double attackAngle;
    private Vec3   movementTarget;

    private static final double APPROACH_DIST   = 160.0;
    private static final double APPROACH_HEIGHT = 25.0;
    private static final double EXIT_DIST       = 160.0;
    private static final double BREAK_HEIGHT    = 30.0;
    private static final double BREAK_DIST      = 80.0;

    public PlaneCombatBehavior(AbstractAerialEntity entity) {
        this.entity      = entity;
        this.attackAngle = entity.getRandom().nextDouble() * Math.PI * 2;
    }

    @Override
    public Vec3 getAttackPosition(LivingEntity target) {
        Vec3 targetPos = target.position();
        Vec3 selfPos   = entity.position();
        double dist    = selfPos.distanceTo(targetPos);

        switch (phase) {

            case APPROACH -> {
                if (movementTarget == null)
                    movementTarget = approachPoint(targetPos);

                if (dist < 100) {
                    // Iniciar pasada: el exit point está al otro lado del target,
                    // a RUN_HEIGHT bloques sobre la altitud actual del objetivo.
                    Vec3 runDir    = targetPos.subtract(selfPos).normalize();
                    double exitY   = targetPos.y + 10; // rasante pero sobre el suelo
                    movementTarget = new Vec3(
                            targetPos.x + runDir.x * EXIT_DIST,
                            exitY,
                            targetPos.z + runDir.z * EXIT_DIST
                    );
                    phase = Phase.FIRE_RUN;
                }
            }

            case FIRE_RUN -> {
                // Una vez pasado el target, romper y subir
                if (dist > 120) {
                    Vec3 breakDir  = selfPos.subtract(targetPos).normalize();
                    movementTarget = new Vec3(
                            selfPos.x + breakDir.x * BREAK_DIST,
                            selfPos.y + BREAK_HEIGHT,   // pull-up tras la pasada
                            selfPos.z + breakDir.z * BREAK_DIST
                    );
                    phase = Phase.BREAK;
                }
            }

            case BREAK -> {
                // Cuando llegamos al punto de break (o nos alejamos suficiente),
                // iniciar nueva aproximación desde ángulo diferente
                boolean arrivedAtBreak = movementTarget != null && selfPos.distanceTo(movementTarget) < 35;
                boolean farEnough      = dist > 180;

                if (arrivedAtBreak || farEnough) {
                    // ~137° — garantiza que las pasadas no sean del mismo lado
                    attackAngle   += Math.PI * 0.76;
                    movementTarget = approachPoint(targetPos);
                    phase          = Phase.APPROACH;
                }
            }
        }

        return movementTarget != null ? movementTarget : targetPos;
    }

    // -------------------------------------------------------------------------

    private Vec3 approachPoint(Vec3 targetPos) {
        double dx = Math.cos(attackAngle) * APPROACH_DIST;
        double dz = Math.sin(attackAngle) * APPROACH_DIST;
        return targetPos.add(dx, APPROACH_HEIGHT, dz);
    }
}
