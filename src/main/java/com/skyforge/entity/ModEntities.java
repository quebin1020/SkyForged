package com.skyforge.entity;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.BasicTurretEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static void register(
            IEventBus eventBus
    ) {

        ENTITIES.register(eventBus);
    }

    public static final DeferredRegister<EntityType<?>>
            ENTITIES =
            DeferredRegister.create(
                    BuiltInRegistries.ENTITY_TYPE,
                    SkyforgeMod.MOD_ID
            );

    public static final DeferredHolder<
            EntityType<?>,
            EntityType<DebugHelicopterEntity>
            > DEBUG_HELICOPTER =
            ENTITIES.register(
                    "debug_helicopter",

                    () -> EntityType.Builder
                            .of(
                                    DebugHelicopterEntity::new,
                                    MobCategory.MONSTER
                            )
                            .sized(
                                    1.5f,
                                    1.5f
                            )
                            .build(
                                    "debug_helicopter"
                            )
            );

    public static final DeferredHolder<
            EntityType<?>,
            EntityType<BasicTurretEntity>
            > BASIC_TURRET =
            ENTITIES.register(
                    "basic_turret",

                    () -> EntityType.Builder
                            .of(
                                    BasicTurretEntity::new,
                                    MobCategory.MONSTER
                            )
                            .sized(
                                    1.0f,
                                    1.5f
                            )
                            .build(
                                    "basic_turret"
                            )
            );
    public static final DeferredHolder<
            EntityType<?>,
            EntityType<PlaneEntity>
            > BASIC_PLANE =
            ENTITIES.register(
                    "basic_plane",

                    () -> EntityType.Builder
                            .of(
                                    PlaneEntity::new,
                                    MobCategory.MONSTER
                            )
                            .sized(
                                    1.0f,
                                    1.5f
                            )
                            .build(
                                    "basic_plane"
                            )
            );
    public static final DeferredHolder<
            EntityType<?>,
            EntityType<AirshipEntity>
            > BASIC_AIRSHIP =
            ENTITIES.register(
                    "basic_airship",

                    () -> EntityType.Builder
                            .of(
                                    AirshipEntity::new,
                                    MobCategory.MONSTER
                            )
                            .sized(
                                    1.0f,
                                    1.5f
                            )
                            .build(
                                    "basic_airship"
                            )
            );
}