package com.lunarini.puzzlekit.gui.uiElement;

import com.lowdragmc.lowdraglib2.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lunarini.puzzlekit.gui.session.DragSession;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class GridItem extends UIElement {
    //字段
    float width;
    float height;
    int zIndex = 200;
    String id = "GridItem";
    Minecraft mc = Minecraft.getInstance();
    public ItemStack itemStack;
    public ItemStack itemStackCopy;
    ItemStackTexture itemStackTexture;
    public int slotRow;
    public int slotColumn;

    public GridItem(DragSession dragSession, ItemStack item){
        //基础操作
        itemStack = item;
        itemStackTexture = new ItemStackTexture(itemStack);

        var location = getItemTexture(itemStack.getItem());
        //处理图片信息
//        try {
//            // 获取资源
//            Resource resource = mc.getResourceManager().getResourceOrThrow(location);
//
//            // 使用 NativeImage 读取
//            try (NativeImage image = NativeImage.read(resource.open())) {
//                width = image.getWidth();
//                height = image.getHeight();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            width = 0;
//            height = 0;
//        }
        width = 20;
        height = 20;

        //设置元素基础属性
        float finalWidth = width;
        float finalHeight = height;
        dragSession.itemWidth = width;
        dragSession.itemHeight = height;

        setId(id);
        layout(layout -> layout
                .width(finalWidth)
                .height(finalHeight)
        );
        style(style -> style
                .background(itemStackTexture)
                .zIndex(zIndex)
        );

        //设置鼠标按下事件
        addEventListener(UIEvents.MOUSE_DOWN, event -> {
            dragSession.success = false;
            ItemStackTexture dragTexture = new ItemStackTexture(itemStack);
            dragTexture.scale(0.5F);
            startDrag(dragSession,dragTexture);
            dragSession.currentSourceSlot = getParent();
            if (getParent() != null) getParent().removeChild(this);
        });

        //设置拖拽结束事件
        addEventListener(UIEvents.DRAG_END,event -> {
            mc.execute(()->{
                if (!dragSession.success){
                    dragSession.currentSourceSlot.addChildren(event.dragHandler.dragSource);
                }
            });
        });
    }

}
