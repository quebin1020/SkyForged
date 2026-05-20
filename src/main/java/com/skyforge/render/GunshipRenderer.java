package com.skyforge.render;

import com.skyforge.entity.GunshipEntity;
import com.skyforge.render.model.GunshipModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GunshipRenderer extends GeoEntityRenderer<GunshipEntity> {

    public GunshipRenderer(EntityRendererProvider.Context context) {
        super(context, new GunshipModel());
    }
}
