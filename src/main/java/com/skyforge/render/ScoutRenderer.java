package com.skyforge.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.skyforge.entity.ScoutEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScoutRenderer extends EntityRenderer<ScoutEntity> {

    public ScoutRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ScoutEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));

        VertexConsumer vertex = buffer.getBuffer(RenderType.lines());

        // Scout: más pequeño y aerodinámico que el avión
        drawBox(poseStack, vertex, 0.35f, 0.18f, 0.8f);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void drawBox(PoseStack poseStack, VertexConsumer vertex, float hw, float hh, float hl) {
        PoseStack.Pose pose = poseStack.last();
        line(vertex, pose, -hw, -hh, -hl,  hw, -hh, -hl);
        line(vertex, pose,  hw, -hh, -hl,  hw, -hh,  hl);
        line(vertex, pose,  hw, -hh,  hl, -hw, -hh,  hl);
        line(vertex, pose, -hw, -hh,  hl, -hw, -hh, -hl);
        line(vertex, pose, -hw,  hh, -hl,  hw,  hh, -hl);
        line(vertex, pose,  hw,  hh, -hl,  hw,  hh,  hl);
        line(vertex, pose,  hw,  hh,  hl, -hw,  hh,  hl);
        line(vertex, pose, -hw,  hh,  hl, -hw,  hh, -hl);
        line(vertex, pose, -hw, -hh, -hl, -hw,  hh, -hl);
        line(vertex, pose,  hw, -hh, -hl,  hw,  hh, -hl);
        line(vertex, pose,  hw, -hh,  hl,  hw,  hh,  hl);
        line(vertex, pose, -hw, -hh,  hl, -hw,  hh,  hl);
    }

    private void line(VertexConsumer v, PoseStack.Pose pose,
                      float x1, float y1, float z1, float x2, float y2, float z2) {
        v.addVertex(pose.pose(), x1, y1, z1).setColor(0, 255, 180, 255).setNormal(0, 1, 0);
        v.addVertex(pose.pose(), x2, y2, z2).setColor(255, 255, 255, 255).setNormal(0, 1, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(ScoutEntity entity) {
        return null;
    }
}
