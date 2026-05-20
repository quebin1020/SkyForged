package com.skyforge.render;

import com.skyforge.entity.PlaneEntity;
import com.skyforge.render.model.PlaneModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlaneRenderer extends GeoEntityRenderer<PlaneEntity> {

    public PlaneRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaneModel());
    }
}
