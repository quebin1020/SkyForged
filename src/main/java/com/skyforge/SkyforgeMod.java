package com.skyforge;

import com.mojang.logging.LogUtils;
import com.skyforge.entity.ModEntities;
import com.skyforge.event.ModEvents;
import com.skyforge.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;

@Mod(SkyforgeMod.MOD_ID)
public class SkyforgeMod {

    public static final Logger LOGGER1 = LogUtils.getLogger();

    public static final String MOD_ID = "skyforge";

    public SkyforgeMod(IEventBus bus) {

        ModEntities.register(bus);
        ModItems.register(bus);

        bus.addListener(ModEvents::registerAttributes);
    }
}