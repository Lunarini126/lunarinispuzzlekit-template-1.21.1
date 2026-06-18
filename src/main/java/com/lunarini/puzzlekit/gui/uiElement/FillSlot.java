package com.lunarini.puzzlekit.gui.uiElement;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lunarini.puzzlekit.gui.session.DragSession;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class FillSlot extends UIElement {
    //字段
    float width;
    float height;
    int zIndex = 200;
    String id = "FillSlot";
    float fillRate = 1F;

    public FillSlot(String path, DragSession dragSession){
        this(path,dragSession,null);
    }

    public FillSlot(String path, DragSession dragSession, UIElement item){
        //基础操作
        ResourceLocation location = ResourceLocation.parse(path);

        //处理图片信息
        Minecraft mc = Minecraft.getInstance();
        try {
            // 获取资源
            Resource resource = mc.getResourceManager().getResourceOrThrow(location);

            // 使用 NativeImage 读取
            try (NativeImage image = NativeImage.read(resource.open())) {
                width = image.getWidth();
                height = image.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
            width = 0;
            height = 0;
        }

        dragSession.slotWidth = width;
        dragSession.slotHeight = height;

        //设置元素基础属性
        float finalWidth = width;
        float finalHeight = height;

        setId(id);
        layout(layout -> layout
                .width(finalWidth)
                .height(finalHeight)
        );
        style(style -> style
                .background(SpriteTexture.of(path))
                .zIndex(zIndex)
        );

        //设置在元素中结束拖拽事件
        addEventListener(UIEvents.DRAG_PERFORM, event -> {
            UIElement source = event.dragHandler.dragSource;
            UIElement target = event.target;
            if (source == target) return;
            float sourceWidth = finalWidth * fillRate;
            float sourceHeight = finalHeight * fillRate;
            if (source != null) source.getLayout().width(sourceWidth).height(sourceHeight);
            if (source != null) source.transform(transform ->
                    transform.translate(-(sourceWidth - finalWidth)/2, -(sourceHeight - finalHeight)/2)
            );
            target.addChildren(source);
            dragSession.success = true;
        });

        //三参独有逻辑
        if (item == null) return;

        float sourceWidth = finalWidth * fillRate;
        float sourceHeight = finalHeight * fillRate;
        item.getLayout().width(sourceWidth).height(sourceHeight);
        item.transform(transform ->
                transform.translate(-(sourceWidth - finalWidth)/2, -(sourceHeight - finalHeight)/2)
        );
        addChildren(item);
    }
}
