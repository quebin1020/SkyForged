package com.skyforge.client;

import com.skyforge.entity.ModEntities;
import com.skyforge.render.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DEBUG_HELICOPTER.get(), HelicopterRenderer::new);
        event.registerEntityRenderer(ModEntities.GUNSHIP.get(),         GunshipRenderer::new);
        event.registerEntityRenderer(ModEntities.BASIC_PLANE.get(),     PlaneRenderer::new);
        event.registerEntityRenderer(ModEntities.BASIC_AIRSHIP.get(),   AirshipRenderer::new);
        event.registerEntityRenderer(ModEntities.SCOUT.get(),           ScoutRenderer::new);
        event.registerEntityRenderer(ModEntities.BASIC_TURRET.get(),    TurretRenderer::new);
    }
}
