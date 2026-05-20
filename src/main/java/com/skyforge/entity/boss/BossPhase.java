package com.skyforge.entity.boss;

import com.skyforge.integration.cbc.CBCAmmoType;

/**
 * Configuración de una fase del boss.
 *
 * hpThreshold     — HP% en que entra esta fase (1.0 = inicio, 0.65 = 65% HP...)
 * overrideAmmo    — null = mantener ammo actual; non-null = reemplazar ammo de todas las torretas
 * speedMultiplier — factor de velocidad aplicado al movement (1.0 = sin cambio)
 * orbitMultiplier — factor de radio/distancia de combate (1.0 = sin cambio)
 * displayMessage  — mensaje en chat al entrar en fase; null = sin mensaje
 */
public record BossPhase(
        float hpThreshold,
        CBCAmmoType overrideAmmo,
        float speedMultiplier,
        float orbitMultiplier,
        String displayMessage
) {
    /** Fase inicial (100% HP, sin cambios). */
    public static BossPhase start() {
        return new BossPhase(1.0f, null, 1.0f, 1.0f, null);
    }

    /** Fase de escalada: cambia ammo y aumenta agresividad. */
    public static BossPhase escalate(float hpThreshold, CBCAmmoType ammo, float speed, float orbit, String msg) {
        return new BossPhase(hpThreshold, ammo, speed, orbit, msg);
    }

    /** Construcción libre. */
    public static BossPhase at(float threshold, CBCAmmoType ammo, float speed, float orbit, String msg) {
        return new BossPhase(threshold, ammo, speed, orbit, msg);
    }
}
