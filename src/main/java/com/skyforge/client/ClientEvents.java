package com.skyforge.client;

import com.skyforge.entity.ModEntities;
import com.skyforge.render.BasicTurretRenderer;
import com.skyforge.render.DebugHelicopterRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {

        event.registerEntityRenderer(
                ModEntities.DEBUG_HELICOPTER.get(),
                DebugHelicopterRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.BASIC_TURRET.get(),
                BasicTurretRenderer::new
        );
    }
}