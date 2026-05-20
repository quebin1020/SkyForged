package com.skyforge.render;

import com.skyforge.entity.boss.BossGunshipEntity;
import com.skyforge.render.model.BossGunshipModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BossGunshipRenderer extends GeoEntityRenderer<BossGunshipEntity> {

    public BossGunshipRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BossGunshipModel());
    }
}
