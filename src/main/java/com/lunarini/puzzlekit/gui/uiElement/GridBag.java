package com.lunarini.puzzlekit.gui.uiElement;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lunarini.puzzlekit.gui.session.DragSession;
import com.mojang.blaze3d.platform.NativeImage;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class GridBag extends UIElement {
    //字段
    ///背包
    float width;
    float height;
    int zIndex = 0;
    float rate = 1.1F;
    String id = "GridBag";
    ///格子
    float slotWidth;
    float slotHeight;
    int GridZIndex = 1;
    int row = 5;
    int column = 5;

    public GridBag(String path){
        //处理图片信息，计算格子大小
        ResourceLocation location = ResourceLocation.parse(path);
        Minecraft mc = Minecraft.getInstance();
        try {
            // 获取资源
            Resource resource = mc.getResourceManager().getResourceOrThrow(location);

            // 使用 NativeImage 读取
            try (NativeImage image = NativeImage.read(resource.open())) {
                slotWidth = image.getWidth();
                slotHeight = image.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
            slotWidth = 0;
            slotHeight = 0;
        }

        //依据格子大小，计算界面大小
        width = (slotWidth * column) * rate;
        height = (slotHeight * row) * rate;

        //界面基础设置
        float finalGridWidth = slotWidth;
        float finalGridHeight = slotHeight;
        layout(layout -> layout
                .width(width)
                .height(height)
                .display(TaffyDisplay.GRID)
                .gridTemplateColumns("repeat(" + column + ", " + finalGridWidth + "px)")
                .gridTemplateRows("repeat(" + row + ", " + finalGridHeight + "px)")
                .justifyContent(AlignContent.CENTER)   // 主轴居中 → 垂直居中
                .alignContent(AlignContent.CENTER)
        );
        style(style -> style.background(Sprites.BORDER));

        //添加格子
        for (int k = 0; k < row; k++) {
            for (int i = 0; i < column; i++) {

                UIElement slot = new UIElement();
                int finalRow = k;
                int finalCol = i;
                slot.layout(layout -> layout
                        .width(finalGridWidth)
                        .height(finalGridHeight)
                        .gridColumn("" + (finalCol+1))
                        .gridRow("" + (finalRow+1))
                );
                // 设置背景图片（使用你提供的 SpriteTexture）
                slot.style(style -> style.background(
                                SpriteTexture.of(path))
                        .zIndex(GridZIndex)
                );
                //在元素上结束拖拽时
                float finalSlotWidth = slotWidth;
                float finalSlotHeight = slotHeight;
                slot.addEventListener(UIEvents.DRAG_PERFORM, e -> {
                    var source = e.dragHandler.dragSource;
                    var target = e.target;
                    if (source == null) return;
                    if (source == target) return;

                    source.layout(layout -> layout
                            .gridColumn("" + (finalCol+1))
                            .gridRow("" + (finalRow+1))
                    );

                    addChildren(source);
                    Minecraft.getInstance().execute(() -> {
                        if (e.dragHandler.draggingObject instanceof DragSession data){
                            float w = data.itemWidth;
                            float h = data.itemHeight;
                            float offsetX = -(w - finalSlotWidth) / 2f;
                            float offsetY = -(h - finalSlotHeight) / 2f;
                            source.transform(transform -> transform.translate(offsetX, offsetY));
                            source.getLayout().width(w).height(h);
                            data.success = true;
                        }
                    });
                });
                // 添加到网格中（自动按顺序填充）
                addChild(slot);
            }
        }
    }
}
