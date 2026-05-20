package com.skyforge.render.model;

import com.skyforge.entity.AbstractTurretEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

/**
 * Base común para los GeoModels de AbstractTurretEntity.
 * Anima los huesos turret_0..turret_3 con los ángulos sincronizados.
 */
public abstract class SkyforgeTurretModel<T extends AbstractTurretEntity & GeoAnimatable>
        extends GeoModel<T> {

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        animateTurrets(entity);
    }

    protected void animateTurrets(T entity) {
        for (int i = 0; i < AbstractTurretEntity.MAX_TURRETS; i++) {
            final int id = i;
            getBone("turret_" + i).ifPresent(bone -> {
                bone.setRotY((float) Math.toRadians(-entity.getTurretYaw(id)));
                bone.setRotX((float) Math.toRadians(entity.getTurretPitch(id)));
            });
        }
    }
}
