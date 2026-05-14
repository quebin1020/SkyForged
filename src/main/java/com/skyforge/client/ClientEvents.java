package com.skyforge.client;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.ModEntities;
import com.skyforge.render.DebugHelicopterRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = SkyforgeMod.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(
                ModEntities.DEBUG_HELICOPTER.get(),
                DebugHelicopterRenderer::new
        );
    }
}