package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.ScoutEntity;
import net.minecraft.resources.ResourceLocation;

public class ScoutModel extends SkyforgeAerialModel<ScoutEntity> {

    @Override
    public ResourceLocation getModelResource(ScoutEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/scout.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScoutEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScoutEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/scout.animation.json");
    }
}
