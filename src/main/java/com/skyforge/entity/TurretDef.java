package com.skyforge.entity;

import com.skyforge.ai.combat.AimController;
import com.skyforge.ai.combat.AimProfile;
import com.skyforge.integration.cbc.CBCAmmoType;
import net.minecraft.world.phys.Vec3;

/**
 * Definición declarativa de una torreta para el sistema builder.
 * Se crea con addTurret() y se procesa en turretInit().
 *
 * localOffset: posición relativa al centro del entity en espacio local
 *   X = derecha (positivo = right del entity)
 *   Y = arriba
 *   Z = adelante (positivo = frente del entity)
 */
public record TurretDef(
        int id,
        AimProfile profile,
        AimController.AimMode aimMode,
        CBCAmmoType ammoType,
        Vec3 localOffset) {
}
