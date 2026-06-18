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
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class GridBag extends UIElement {
    //字段
    //背包
    float width;
    float height;
    int zIndex = 0;
    float rate = 1.1F;
    String id = "GridBag";
    GridItem[][] bagGrids;
    //格子
    float slotWidth;
    float slotHeight;
    int GridZIndex = 1;
    int row = 5;
    int column = 5;
    Player player;

    public GridBag(String path,DragSession dragSession){
        //处理图片信息，计算格子大小
        ResourceLocation location = ResourceLocation.parse(path);
        Minecraft mc = Minecraft.getInstance();
        player = dragSession.player;
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
        //初始化网格存储信息
        bagGrids = new GridItem[column][row];

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

        //打开UI时读取玩家身上的物品
        var inventory = player.getInventory();

        for (int a = 0; a < inventory.getContainerSize(); a++) {
            ItemStack stack = inventory.getItem(a);
            int col = 0;
            int row = 0;
            if (!stack.isEmpty()) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    CompoundTag nbt = customData.copyTag();
                    col = nbt.getInt("column");
                    row = nbt.getInt("row");
                }

                //如果是0，说明该物品不属于本背包系统添加
                if (col == 0) continue;

                //在UI中添加对应物品
                ItemStack uiItemStack = stack.copy();
                var gridItem = new GridItem(dragSession,uiItemStack);
                gridItem.itemStackCopy = stack;
                int finalCol = col;
                int finalRow = row;
                gridItem.layout(layout -> layout
                        .gridColumn("" + (finalCol))
                        .gridRow("" + (finalRow))
                );
                //设置新占用
                gridItem.slotRow = finalRow;
                gridItem.slotColumn = finalCol;
                bagGrids[finalCol-1][finalRow-1] = gridItem;

                float finalSlotWidth = slotWidth;
                float finalSlotHeight = slotHeight;

                float w = dragSession.itemWidth;
                float h = dragSession.itemHeight;
                float offsetX = -(w - finalSlotWidth) / 2f;
                float offsetY = -(h - finalSlotHeight) / 2f;
                gridItem.transform(transform -> transform.translate(offsetX, offsetY));
                gridItem.getLayout().width(w).height(h);

                addChild(gridItem);
            }
        }

        //添加格子
        for (int k = 0; k < row; k++) {
            for (int i = 0; i < column; i++) {

                UIElement slot = new UIElement();
                int finalRow = k + 1;
                int finalCol = i + 1;
                slot.layout(layout -> layout
                        .width(finalGridWidth)
                        .height(finalGridHeight)
                        .gridColumn("" + (finalCol))
                        .gridRow("" + (finalRow))
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

                    //如果该格已经被占有，则返回
                    if (bagGrids[finalCol-1][finalRow-1] != null) return;

                    var source = e.dragHandler.dragSource;
                    var target = e.target;
                    if (source == null) return;
                    if (source == target) return;

                    source.layout(layout -> layout
                            .gridColumn("" + (finalCol))
                            .gridRow("" + (finalRow))
                    );

                    addChildren(source);

                    //设置网格相关属性
                    if (source instanceof GridItem gridItem){
                        //撤销老占用
                        if (source.getParent() instanceof GridBag parent){
                            if (gridItem.slotColumn - 1 >= 0 && gridItem.slotRow - 1 >= 0){
                                parent.bagGrids[gridItem.slotColumn - 1][gridItem.slotRow - 1] = null;
                            }
                        }

                        //设置新占用
                        gridItem.slotRow = finalRow;
                        gridItem.slotColumn = finalCol;
                        bagGrids[finalCol-1][finalRow-1] = gridItem;

                        //为玩家添加物品

                        if (gridItem.itemStackCopy == null){
                            // 遍历主背包，在第一个空位添加复制物品给玩家
                            gridItem.itemStackCopy = gridItem.itemStack.copy();
                            CompoundTag nbt = new CompoundTag();
                            nbt.putInt("column",finalCol);
                            nbt.putInt("row",finalRow);
                            gridItem.itemStackCopy.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                            for (int a = 0; a < inventory.getContainerSize(); a++) {
                                ItemStack stack = inventory.getItem(a);
                                if (stack.isEmpty()) {
                                    inventory.add(a, gridItem.itemStackCopy);
                                    break;
                                }
                            }
                        }
                    }


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
