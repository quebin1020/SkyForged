package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.MissileHelicopterEntity;
import net.minecraft.resources.ResourceLocation;

public class MissileHelicopterModel extends SkyforgeAerialModel<MissileHelicopterEntity> {

    @Override
    public ResourceLocation getModelResource(MissileHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/missile_helicopter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MissileHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MissileHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/missile_helicopter.animation.json");
    }
}
