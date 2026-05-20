package com.skyforge.render;

import com.skyforge.entity.HeavyDropshipEntity;
import com.skyforge.render.model.DropshipModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HeavyDropshipRenderer extends GeoEntityRenderer<HeavyDropshipEntity> {

    public HeavyDropshipRenderer(EntityRendererProvider.Context context) {
        super(context, new DropshipModel<>("geo/heavy_dropship.geo.json", "animations/heavy_dropship.animation.json"));
    }
}
