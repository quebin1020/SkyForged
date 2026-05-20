package com.skyforge.render.model;

import com.skyforge.entity.boss.BossGunshipEntity;
import net.minecraft.resources.ResourceLocation;

public class BossGunshipModel extends SkyforgeAerialModel<BossGunshipEntity> {

    private static final ResourceLocation MODEL  =
            ResourceLocation.fromNamespaceAndPath("skyforge", "geo/boss_gunship.geo.json");
    private static final ResourceLocation TEX    =
            ResourceLocation.fromNamespaceAndPath("skyforge", "textures/entity/helicopter.png");
    private static final ResourceLocation ANIM   =
            ResourceLocation.fromNamespaceAndPath("skyforge", "animations/boss_gunship.animation.json");

    @Override public ResourceLocation getModelResource(BossGunshipEntity e)     { return MODEL; }
    @Override public ResourceLocation getTextureResource(BossGunshipEntity e)   { return TEX; }
    @Override public ResourceLocation getAnimationResource(BossGunshipEntity e) { return ANIM; }
}
