package com.skyforge.render;

import com.skyforge.entity.BomberEntity;
import com.skyforge.render.model.BomberModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BomberRenderer extends GeoEntityRenderer<BomberEntity> {

    public BomberRenderer(EntityRendererProvider.Context context) {
        super(context, new BomberModel());
    }
}
