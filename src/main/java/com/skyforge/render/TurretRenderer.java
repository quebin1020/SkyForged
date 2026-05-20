package com.skyforge.render;

import com.skyforge.entity.BasicTurretEntity;
import com.skyforge.render.model.TurretModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TurretRenderer extends GeoEntityRenderer<BasicTurretEntity> {

    public TurretRenderer(EntityRendererProvider.Context context) {
        super(context, new TurretModel());
    }
}
