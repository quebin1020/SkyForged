package com.skyforge.render;

import com.skyforge.entity.DropshipEntity;
import com.skyforge.render.model.DropshipModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DropshipRenderer extends GeoEntityRenderer<DropshipEntity> {

    public DropshipRenderer(EntityRendererProvider.Context context) {
        super(context, new DropshipModel<>("geo/dropship.geo.json", "animations/dropship.animation.json"));
    }
}
