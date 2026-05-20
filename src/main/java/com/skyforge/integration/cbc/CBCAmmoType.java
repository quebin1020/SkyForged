package com.skyforge.integration.cbc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Tipos de munición disponibles para asignar a torretas.
 * Cada torreta lleva su CBCAmmoType; llamar a fire() despacha al proyectil correcto.
 */
public enum CBCAmmoType {

    MACHINE_GUN_BULLET {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireMachineGunBullet(level, owner, origin, dir, charge, spread);
        }
    },
    SOLID_SHOT {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireSolidShot(level, owner, origin, dir, charge, spread);
        }
    },
    HE_SHELL {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireHEShell(level, owner, origin, dir, charge, spread);
        }
    },
    AP_SHELL {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireAPShell(level, owner, origin, dir, charge, spread);
        }
    },
    AP_SHOT {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireAPShot(level, owner, origin, dir, charge, spread);
        }
    },
    AP_AUTOCANNON {
        @Override
        public void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
            CBCProjectiles.fireAPAutocannon(level, owner, origin, dir, charge, spread);
        }
    };

    public abstract void fire(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread);
}
