package com.skyforge.ai.combat;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Comportamiento de combate del scout.
 *
 * El scout hace pasadas rasantes: se aproxima al objetivo desde un ángulo
 * orbital que va rotando, a baja altitud. Al pasar sobre el target dispara
 * su metralladora. Luego el comportamiento lo redirige para otro pase.
 *
 * Diferencia clave vs HelicopterCombatBehavior:
 *  - Altitud baja (5-10 bloques sobre el target)
 *  - Distancia de aproximación menor (60 bloques)
 *  - Órbita rápida → más pases por minuto
 */
public class ScoutCombatBehavior implements CombatBehavior {

    private final AbstractAerialEntity entity;

    private double approachAngle;
    private static final double ORBIT_SPEED     = 0.025;  // más rápido que helicóptero
    private static final double APPROACH_RADIUS = 60.0;
    private static final double STRAFE_HEIGHT   = 7.0;    // bajo, rasante

    public ScoutCombatBehavior(AbstractAerialEntity entity) {
        this.entity = entity;
        // Cada scout empieza en un ángulo distinto para que no se apilen
        this.approachAngle = entity.getRandom().nextDouble() * Math.PI * 2;
    }

    @Override
    public Vec3 getAttackPosition(LivingEntity target) {
        approachAngle += ORBIT_SPEED;

        Vec3 targetPos = target.position();

        // Puntos de aproximación opuestos al ángulo actual (el scout PASA por encima del target)
        double dx = Math.cos(approachAngle) * APPROACH_RADIUS;
        double dz = Math.sin(approachAngle) * APPROACH_RADIUS;

        // Altitud rasante — el avión vuela casi al nivel del objetivo
        return targetPos.add(dx, STRAFE_HEIGHT, dz);
    }
}
