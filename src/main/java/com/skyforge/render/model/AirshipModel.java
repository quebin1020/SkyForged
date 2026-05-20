package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.AirshipEntity;
import net.minecraft.resources.ResourceLocation;

public class AirshipModel extends SkyforgeAerialModel<AirshipEntity> {

    @Override
    public ResourceLocation getModelResource(AirshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/airship.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AirshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AirshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/airship.animation.json");
    }
}
