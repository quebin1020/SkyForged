package com.skyforge.render.model;

import com.skyforge.entity.AbstractAerialEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * Base común para todos los GeoModels de AbstractAerialEntity.
 * Anima automáticamente los huesos turret_0..turret_7 usando los
 * ángulos sincronizados del entity via EntityData.
 *
 * Subclases solo necesitan implementar getModelResource,
 * getTextureResource y getAnimationResource.
 */
public abstract class SkyforgeAerialModel<T extends AbstractAerialEntity & GeoAnimatable>
        extends GeoModel<T> {

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        animateTurrets(entity);
    }

    protected void animateTurrets(T entity) {
        for (int i = 0; i < AbstractAerialEntity.MAX_TURRETS; i++) {
            final int id = i;
            getBone("turret_" + i).ifPresent(bone -> {
                bone.setRotY((float) Math.toRadians(-entity.getTurretYaw(id)));
                bone.setRotX((float) Math.toRadians(-entity.getTurretPitch(id)));
            });
        }
    }
}
