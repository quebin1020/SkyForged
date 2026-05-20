package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.GunshipEntity;
import net.minecraft.resources.ResourceLocation;

public class GunshipModel extends SkyforgeAerialModel<GunshipEntity> {

    @Override
    public ResourceLocation getModelResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/gunship.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/gunship.animation.json");
    }
}
