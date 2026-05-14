package com.skyforge.item;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, SkyforgeMod.MOD_ID);

    public static final DeferredHolder<Item, SpawnEggItem> DEBUG_HELICOPTER_SPAWN_EGG =
            ITEMS.register("debug_helicopter_spawn_egg",
                    () -> new SpawnEggItem(
                            ModEntities.DEBUG_HELICOPTER.get(),
                            0x555555,
                            0xff0000,
                            new Item.Properties()
                    ));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}