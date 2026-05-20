package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.BasicTurretEntity;
import net.minecraft.resources.ResourceLocation;

public class TurretModel extends SkyforgeTurretModel<BasicTurretEntity> {

    @Override
    public ResourceLocation getModelResource(BasicTurretEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/turret.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BasicTurretEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BasicTurretEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/turret.animation.json");
    }
}
