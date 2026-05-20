package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.DebugHelicopterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HelicopterModel extends GeoModel<DebugHelicopterEntity> {

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

    @Override
    public void setCustomAnimations(DebugHelicopterEntity entity, long instanceId,
                                    AnimationState<DebugHelicopterEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        // Si el cañón gira al revés, invierte el signo de getTurretYaw(0)
        getBone("turret_0").ifPresent(bone -> {
            bone.setRotY((float) Math.toRadians(-entity.getTurretYaw(0)));
            bone.setRotX((float) Math.toRadians(entity.getTurretPitch(0)));
        });
    }
}
