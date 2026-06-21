package com.lunarini.puzzlekit.gui.uiElement;

import com.lowdragmc.lowdraglib2.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lunarini.puzzlekit.Config;
import com.lunarini.puzzlekit.gui.ItemFrameLikeTexture;
import com.lunarini.puzzlekit.gui.ItemScaleConfig;
import com.lunarini.puzzlekit.gui.session.DragSession;
import dev.vfyjxf.taffy.style.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.guieffect.qual.UI;

import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class GridItem extends UIElement {
    //字段
    float width;
    float height;
    int zIndex = 200;
    int slotNumber = -1;
    String id = "GridItem";
    Minecraft mc = Minecraft.getInstance();
    public ItemStack itemStack;
    public ItemStack itemStackCopy;
    ItemFrameLikeTexture itemStackTexture;
    public int slotRow;
    public int slotColumn;

    public GridItem(DragSession dragSession, ItemStack item){

        var itemTexture = new UIElement();
        //基础操作
        itemStack = item;
        itemStackTexture = new ItemFrameLikeTexture(itemStack);

        ResourceLocation path1 = itemStack.getItem().builtInRegistryHolder().getKey().location();
        ResourceLocation path2 = ResourceLocation.parse("tacz:modern_kinetic_gun");

        if (path1.equals(path2)){
            width = Config.GRID_SLOT_WIDTH.get().floatValue() * 1.4F;
            height = Config.GRID_SLOT_HEIGHT.get().floatValue() * 1.4F;
            itemTexture.transform(t -> t.translate(0,-5));
        }else{
            width = Config.GRID_SLOT_WIDTH.get().floatValue() * ItemScaleConfig.getInstance().get(itemStack).width() * 0.8F;
            height = Config.GRID_SLOT_HEIGHT.get().floatValue() * ItemScaleConfig.getInstance().get(itemStack).height() * 0.8F;
        }


        //设置元素基础属性
        float finalWidth = width;
        float finalHeight = height;
        dragSession.itemWidth = width;
        dragSession.itemHeight = height;

        setId(id);
        itemTexture.layout(layout -> layout
                .width(finalWidth)
                .height(finalHeight)
        );
        itemTexture.style(style -> style
                .background(itemStackTexture)
                .zIndex(zIndex)
        );

        //创建背景板
        var backElement = new UIElement();
        backElement.layout(layout -> layout
                .width(Config.GRID_SLOT_WIDTH.get().floatValue() * ItemScaleConfig.getInstance().get(itemStack).width())
                .height(Config.GRID_SLOT_HEIGHT.get().floatValue() * ItemScaleConfig.getInstance().get(itemStack).height())
        );
        backElement.style(style -> style
                .background(Sprites.BORDER)
                .zIndex(10)
        );

        //设置鼠标按下事件
        addEventListener(UIEvents.MOUSE_DOWN, event -> {
            dragSession.success = false;
            ItemStackTexture dragTexture = new ItemStackTexture(itemStack);
            dragTexture.scale(ItemScaleConfig.getInstance().get(itemStack).width()/2,ItemScaleConfig.getInstance().get(itemStack).height()/2);
            startDrag(dragSession,dragTexture);
            dragSession.currentSourceSlot = getParent();
            if (getParent() != null) getParent().removeChild(this);
        });

        //设置拖拽结束事件
        addEventListener(UIEvents.DRAG_END,event -> {
            ModularUI modularUI = this.getModularUI();

            // 获取拖拽结束时的鼠标位置
            float mouseX = event.x;
            float mouseY = event.y;

            // 找到 GridBag
            UIElement parent = dragSession.gridBag;

            while (parent != null && !(parent instanceof GridBag)) {
                parent = parent.getParent();
            }

            if (parent instanceof GridBag gridBag) {
                gridBag.tryPlaceItemAt(this, mouseX, mouseY, event);
            }

            mc.execute(()->{
                if (!dragSession.success){
                    dragSession.currentSourceSlot.addChildren(event.dragHandler.dragSource);
                }
            });
        });

        backElement.getLayout().positionType(TaffyPosition.ABSOLUTE);
        itemTexture.getLayout().positionType(TaffyPosition.ABSOLUTE);

        backElement.layout(layout -> layout
                .display(TaffyDisplay.FLEX)
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)   // 主轴居中 → 垂直居中
                .alignItems(AlignItems.CENTER)
        );
        addChildren(backElement);
        backElement.addChildren(itemTexture);
    }

}
