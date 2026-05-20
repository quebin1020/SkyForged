package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.GunshipEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class GunshipModel extends GeoModel<GunshipEntity> {

    @Override
    public ResourceLocation getModelResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "geo/helicopter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GunshipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "animations/helicopter.animation.json");
    }

    @Override
    public void setCustomAnimations(GunshipEntity entity, long instanceId,
                                    AnimationState<GunshipEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        getBone("turret_0").ifPresent(bone -> {
            bone.setRotY((float) Math.toRadians(-entity.getTurretYaw(0)));
            bone.setRotX((float) Math.toRadians(entity.getTurretPitch(0)));
        });
    }
}
