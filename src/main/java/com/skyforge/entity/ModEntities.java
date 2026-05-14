package com.skyforge.entity;

import com.skyforge.SkyforgeMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SkyforgeMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DebugHelicopterEntity>> DEBUG_HELICOPTER =
            ENTITIES.register("debug_helicopter",
                    () -> EntityType.Builder.of(DebugHelicopterEntity::new, MobCategory.MONSTER)
                            .sized(2.0f, 1.5f)
                            .build("debug_helicopter"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}