package com.lunarini.puzzlekit.gui;

import com.lowdragmc.lowdraglib2.gui.texture.TransformTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class ItemFrameLikeTexture extends TransformTexture {

    private ItemStack itemStack = ItemStack.EMPTY;

    public ItemFrameLikeTexture(ItemStack stack) {
        this.itemStack = stack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawInternal(GuiGraphics graphics, float mouseX, float mouseY,
                                float x, float y, float width, float height, float partialTicks) {
        if (itemStack.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        PoseStack pose = graphics.pose();

        graphics.flush();
        pose.pushPose();

//        RenderSystem.setShaderColor(2.0F, 2.0F, 2.0F, 1.0F);

        // 启用深度测试
        RenderSystem.enableDepthTest();

        // 设置光照方向
// 方案 A：模拟白天直射光
//        Vector3f light1 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();  // 当前
//        Vector3f light2 = new Vector3f(-0.2F, -1.0F, 0.7F).normalize();

//// 方案 B：正上方强光源
//        Vector3f light1 = new Vector3f(0.0F, 1.0F, 0.0F).normalize();
//        Vector3f light2 = new Vector3f(0.0F, -0.2F, 0.0F).normalize();
//
//// 方案 C：双强光源
        Vector3f light1 = new Vector3f(1F, 1F, 1F).normalize();
        Vector3f light2 = new Vector3f(-0.5F, 0.8F, 1F).normalize();
        RenderSystem.setShaderLights(light1, light2);

        // 设置位置和缩放
        pose.translate(x + width / 2, y + height / 2, 100);
        pose.scale(width, -height, width);
        pose.mulPose(new org.joml.Quaternionf().rotateY((float) Math.PI));

        // 获取模型
        BakedModel model = mc.getItemRenderer().getModel(itemStack, null, null, 0);

        // 渲染
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        mc.getItemRenderer().render(
                itemStack,
                ItemDisplayContext.FIXED,
                false,
                pose,
                buffer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );

        buffer.endBatch();
        pose.popPose();
    }
}