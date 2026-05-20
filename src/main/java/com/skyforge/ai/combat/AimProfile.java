package com.skyforge.ai.combat;

public class AimProfile {

    // si puede rotar libremente el cañón
    public final boolean canRotate;

    // velocidad de tracking
    public final double aimSpeed;

    // velocidad del proyectil (predicción)
    public final double projectileSpeed;

    // tolerancia de disparo (en grados)
    public final double firingTolerance;

    // inaccuracy
    public final double baseInaccuracy;

    // si usa predicción
    public final boolean predictive;

    public AimProfile(
            boolean canRotate,
            double aimSpeed,
            double projectileSpeed,
            double firingTolerance,
            double baseInaccuracy,
            boolean predictive
    ) {
        this.canRotate = canRotate;
        this.aimSpeed = aimSpeed;
        this.projectileSpeed = projectileSpeed;
        this.firingTolerance = firingTolerance;
        this.baseInaccuracy = baseInaccuracy;
        this.predictive = predictive;
    }

    public static AimProfile HELICOPTER = new AimProfile(
            true,
            0.15,
            2.0,
            8,
            0.05,
            true
    );

    public static AimProfile TURRET = new AimProfile(
            true,
            0.08,
            3.0,
            6,
            0.03,
            true
    );

    public static AimProfile AIRPLANE = new AimProfile(
            false,   // no gira cañón
            0.0,     // irrelevante
            3.5,
            2.0,     // MUY estricto (casi frontal)
            0.01,
            true
    );

    /**
     * Metralladora ligera (scout).
     * Proyectil muy rápido → predicción simple.
     * Tolerancia más amplia que AIRPLANE porque se dispara en ráfaga.
     */
    public static AimProfile MACHINE_GUN = new AimProfile(
            false,   // fixed gun, el cuerpo de la nave apunta
            0.0,
            6.0,     // bala rápida
            6.0,     // ~6° de tolerancia (ráfaga compensa la imprecisión)
            0.04,    // leve dispersión tipo spread
            true
    );
}