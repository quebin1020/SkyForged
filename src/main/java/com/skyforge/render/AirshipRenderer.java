package com.skyforge.render;

import com.skyforge.entity.AirshipEntity;
import com.skyforge.render.model.AirshipModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AirshipRenderer extends GeoEntityRenderer<AirshipEntity> {

    public AirshipRenderer(EntityRendererProvider.Context context) {
        super(context, new AirshipModel());
    }
}
