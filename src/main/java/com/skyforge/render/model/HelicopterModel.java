package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.DebugHelicopterEntity;
import net.minecraft.resources.ResourceLocation;

public class HelicopterModel extends SkyforgeAerialModel<DebugHelicopterEntity> {

    @Override
    public ResourceLocation getModelResource(DebugHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/helicopter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DebugHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DebugHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/helicopter.animation.json");
    }
}
