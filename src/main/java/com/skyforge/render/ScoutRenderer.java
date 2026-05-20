package com.skyforge.render;

import com.skyforge.entity.ScoutEntity;
import com.skyforge.render.model.ScoutModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScoutRenderer extends GeoEntityRenderer<ScoutEntity> {

    public ScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new ScoutModel());
    }
}
