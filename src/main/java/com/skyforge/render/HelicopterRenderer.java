package com.skyforge.render;

import com.skyforge.entity.DebugHelicopterEntity;
import com.skyforge.render.model.HelicopterModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HelicopterRenderer extends GeoEntityRenderer<DebugHelicopterEntity> {

    public HelicopterRenderer(EntityRendererProvider.Context context) {
        super(context, new HelicopterModel());
    }
}
