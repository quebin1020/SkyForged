package com.skyforge.entity;

import com.skyforge.SkyforgeMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Tamaños de hitbox — referencia de modelo en bloques (16 unidades = 1 bloque):
 *
 *  helicopter  → modelo ~10 bloques ancho (rotor), 4 alto. Hitbox 6×3.5
 *  gunship     → modelo ~12 bloques ancho (rotor), 5 alto. Hitbox 7×4
 *  plane       → modelo ~14 bloques envergadura, 2 alto.  Hitbox 8×2
 *  airship     → modelo ~12×9 bloques.                    Hitbox 9×5
 *  scout       → modelo ~10 bloques envergadura, 1.5 alto.Hitbox 5×1.5
 *  turret      → base 2.5×3 bloques                       Hitbox 2.5×3
 */
public class ModEntities {

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SkyforgeMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DebugHelicopterEntity>> DEBUG_HELICOPTER =
            ENTITIES.register("debug_helicopter", () -> EntityType.Builder
                    .of(DebugHelicopterEntity::new, MobCategory.MONSTER)
                    .sized(6.0f, 3.5f)
                    .build("debug_helicopter"));

    public static final DeferredHolder<EntityType<?>, EntityType<BasicTurretEntity>> BASIC_TURRET =
            ENTITIES.register("basic_turret", () -> EntityType.Builder
                    .of(BasicTurretEntity::new, MobCategory.MONSTER)
                    .sized(2.5f, 3.0f)
                    .build("basic_turret"));

    public static final DeferredHolder<EntityType<?>, EntityType<PlaneEntity>> BASIC_PLANE =
            ENTITIES.register("basic_plane", () -> EntityType.Builder
                    .of(PlaneEntity::new, MobCategory.MONSTER)
                    .sized(8.0f, 2.0f)
                    .build("basic_plane"));

    public static final DeferredHolder<EntityType<?>, EntityType<AirshipEntity>> BASIC_AIRSHIP =
            ENTITIES.register("basic_airship", () -> EntityType.Builder
                    .of(AirshipEntity::new, MobCategory.MONSTER)
                    .sized(9.0f, 5.0f)
                    .build("basic_airship"));

    public static final DeferredHolder<EntityType<?>, EntityType<ScoutEntity>> SCOUT =
            ENTITIES.register("scout", () -> EntityType.Builder
                    .of(ScoutEntity::new, MobCategory.MONSTER)
                    .sized(5.0f, 1.5f)
                    .build("scout"));

    public static final DeferredHolder<EntityType<?>, EntityType<GunshipEntity>> GUNSHIP =
            ENTITIES.register("gunship", () -> EntityType.Builder
                    .of(GunshipEntity::new, MobCategory.MONSTER)
                    .sized(7.0f, 4.0f)
                    .build("gunship"));
}
