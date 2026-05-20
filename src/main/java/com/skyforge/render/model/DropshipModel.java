package com.skyforge.render.model;

import com.skyforge.SkyforgeMod;
import com.skyforge.entity.AbstractDropshipEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;

public class DropshipModel<T extends AbstractDropshipEntity & GeoAnimatable>
        extends SkyforgeAerialModel<T> {

    private final String geoPath;
    private final String animPath;

    public DropshipModel(String geoPath, String animPath) {
        this.geoPath  = geoPath;
        this.animPath = animPath;
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, geoPath);
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, "textures/entity/helicopter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return ResourceLocation.fromNamespaceAndPath(SkyforgeMod.MOD_ID, animPath);
    }
}
