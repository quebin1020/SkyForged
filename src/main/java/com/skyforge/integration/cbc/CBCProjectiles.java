package com.skyforge.integration.cbc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/**
 * Fachada para lanzar proyectiles de Create Big Cannons.
 *
 * Todos los proyectiles CBC extienden Projectile (vanilla), así que usamos
 * ese tipo común para evitar depender de Registrate (EntityEntry).
 *
 * Registry names confirmados del jar:
 *   Big cannon : shot, he_shell, ap_shell, ap_shot, shrapnel_shell
 *   Autocannon : machine_gun_bullet, ap_autocannon, flak_autocannon
 *
 * SOLO llamar desde ServerLevel.
 */
public final class CBCProjectiles {

    private static final String CBC = "createbigcannons";

    private CBCProjectiles() {}

    // -------------------------------------------------------------------------
    //  Big Cannon
    // -------------------------------------------------------------------------

    /** Proyectil sólido — alto impacto, sin explosión. Helicóptero/torreta. */
    public static void fireSolidShot(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("shot", level, owner, origin, dir, charge, spread);
    }

    /** Proyectil HE — explosivo al impacto. Avión de ataque, airship. */
    public static void fireHEShell(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("he_shell", level, owner, origin, dir, charge, spread);
    }

    /** Proyectil AP (Armor Piercing) — penetra armadura. */
    public static void fireAPShell(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("ap_shell", level, owner, origin, dir, charge, spread);
    }

    /**
     * AP Shot — más ligero que AP Shell, sin explosión, rápido.
     * Ideal para burst fire de aviones (ráfaga sin destrucción masiva).
     */
    public static void fireAPShot(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("ap_shot", level, owner, origin, dir, charge, spread);
    }

    // -------------------------------------------------------------------------
    //  Autocannon (metralladora ligera)
    // -------------------------------------------------------------------------

    /**
     * Bala de metralladora — ligera, rápida, para ráfagas. Scout.
     * Charge alta (4-5) para dar sensación de velocidad de bala.
     */
    public static void fireMachineGunBullet(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("machine_gun_bullet", level, owner, origin, dir, charge, spread);
    }

    /** Ronda AP de autocañón — para scout con penetración. */
    public static void fireAPAutocannon(ServerLevel level, Entity owner, Vec3 origin, Vec3 dir, float charge, float spread) {
        spawn("ap_autocannon", level, owner, origin, dir, charge, spread);
    }

    // -------------------------------------------------------------------------
    //  Internal
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static void spawn(
            String name,
            ServerLevel level,
            Entity owner,
            Vec3 origin,
            Vec3 direction,
            float charge,
            float spread
    ) {
        EntityType<?> rawType = BuiltInRegistries.ENTITY_TYPE.get(
                ResourceLocation.fromNamespaceAndPath(CBC, name)
        );
        if (!(rawType instanceof EntityType)) return;

        // Todos los proyectiles CBC extienden Projectile (vanilla)
        EntityType<? extends Projectile> type = (EntityType<? extends Projectile>) rawType;

        Projectile projectile = type.create(level);
        if (projectile == null) return;

        projectile.setPos(origin);

        Vec3 dir = direction.normalize();
        projectile.shoot(dir.x, dir.y, dir.z, charge, spread);

        projectile.xRotO = projectile.getXRot();
        projectile.yRotO = projectile.getYRot();

        if (owner != null) projectile.setOwner(owner);

        level.addFreshEntity(projectile);
    }
}
