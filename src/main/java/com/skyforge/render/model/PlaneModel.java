package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.PlaneEntity;
import net.minecraft.resources.ResourceLocation;

public class PlaneModel extends SkyforgeAerialModel<PlaneEntity> {

    @Override
    public ResourceLocation getModelResource(PlaneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/airplane.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlaneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlaneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/airplane.animation.json");
    }
}
