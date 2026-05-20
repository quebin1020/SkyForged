package com.skyforge.entity;

import com.skyforge.SkyforgeMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SkyforgeMod.MOD_ID);

    // Helicóptero — cuerpo compacto, rotor ancho
    // sized(width, height): width = diámetro del cilindro, height = altura total
    public static final DeferredHolder<EntityType<?>, EntityType<DebugHelicopterEntity>> DEBUG_HELICOPTER =
            ENTITIES.register("debug_helicopter", () -> EntityType.Builder
                    .of(DebugHelicopterEntity::new, MobCategory.MONSTER)
                    .sized(3.5f, 1.8f)
                    .build("debug_helicopter"));

    // Torreta — base ancha, cañón alto
    public static final DeferredHolder<EntityType<?>, EntityType<BasicTurretEntity>> BASIC_TURRET =
            ENTITIES.register("basic_turret", () -> EntityType.Builder
                    .of(BasicTurretEntity::new, MobCategory.MONSTER)
                    .sized(1.8f, 2.2f)
                    .build("basic_turret"));

    // Avión pesado — envergadura grande, fuselaje plano
    public static final DeferredHolder<EntityType<?>, EntityType<PlaneEntity>> BASIC_PLANE =
            ENTITIES.register("basic_plane", () -> EntityType.Builder
                    .of(PlaneEntity::new, MobCategory.MONSTER)
                    .sized(5.0f, 1.5f)
                    .build("basic_plane"));

    // Airship — el más grande, voluminoso
    public static final DeferredHolder<EntityType<?>, EntityType<AirshipEntity>> BASIC_AIRSHIP =
            ENTITIES.register("basic_airship", () -> EntityType.Builder
                    .of(AirshipEntity::new, MobCategory.MONSTER)
                    .sized(7.0f, 3.5f)
                    .build("basic_airship"));

    // Scout — ágil y pequeño, menor que el avión pesado
    public static final DeferredHolder<EntityType<?>, EntityType<ScoutEntity>> SCOUT =
            ENTITIES.register("scout", () -> EntityType.Builder
                    .of(ScoutEntity::new, MobCategory.MONSTER)
                    .sized(3.0f, 1.2f)
                    .build("scout"));

    // Gunship — helicóptero de combate pesado, mob real de encuentro
    public static final DeferredHolder<EntityType<?>, EntityType<GunshipEntity>> GUNSHIP =
            ENTITIES.register("gunship", () -> EntityType.Builder
                    .of(GunshipEntity::new, MobCategory.MONSTER)
                    .sized(5.0f, 2.5f)
                    .build("gunship"));
}
