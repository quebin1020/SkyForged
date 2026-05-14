package com.skyforge.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skyforge.SkyforgeMod;
import com.skyforge.entity.DebugHelicopterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DebugHelicopterRenderer extends EntityRenderer<DebugHelicopterEntity> {

    public DebugHelicopterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            DebugHelicopterEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        // después:
        // render cubo debug
        // líneas
        // vectores
    }

    @Override
    public ResourceLocation getTextureLocation(DebugHelicopterEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                SkyforgeMod.MOD_ID,
                "textures/entity/debug_helicopter.png"
        );
    }
}