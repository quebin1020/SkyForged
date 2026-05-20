package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.BomberEntity;
import net.minecraft.resources.ResourceLocation;

public class BomberModel extends SkyforgeAerialModel<BomberEntity> {

    @Override
    public ResourceLocation getModelResource(BomberEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/bomber.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BomberEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BomberEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/bomber.animation.json");
    }
}
