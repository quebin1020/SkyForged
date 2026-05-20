package com.skyforge.render;

import com.skyforge.entity.MissileHelicopterEntity;
import com.skyforge.render.model.MissileHelicopterModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MissileHelicopterRenderer extends GeoEntityRenderer<MissileHelicopterEntity> {

    public MissileHelicopterRenderer(EntityRendererProvider.Context context) {
        super(context, new MissileHelicopterModel());
    }
}
