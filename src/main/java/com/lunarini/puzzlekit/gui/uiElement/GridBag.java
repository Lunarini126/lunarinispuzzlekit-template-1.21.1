package com.lunarini.puzzlekit.gui.uiElement;

import com.lowdragmc.lowdraglib2.gui.sync.rpc.RPCEventBuilder;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lunarini.puzzlekit.Config;
import com.lunarini.puzzlekit.gui.ItemScaleConfig;
import com.lunarini.puzzlekit.gui.session.DragSession;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
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
    float slotWidth = Config.GRID_SLOT_WIDTH.get().floatValue();
    float slotHeight = Config.GRID_SLOT_WIDTH.get().floatValue();
    int GridZIndex = 1;
    int row = 5;
    int column = 5;
    //其他逻辑字段
    Player player;
    // 存储所有 slot 引用
    private UIElement[][] slotRefs;

    public GridBag(String path,DragSession dragSession){
        //数据初始化
        player = dragSession.player;
        var inventory = player.getInventory();
        bagGrids = new GridItem[column][row];
        slotWidth = Config.GRID_SLOT_WIDTH.get().floatValue();
        slotHeight = Config.GRID_SLOT_HEIGHT.get().floatValue();
        slotRefs = new UIElement[column][row];

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

                //创建格子并设置基础信息
                UIElement slot = createSlot(k,i,path);
                slot.getStyle().zIndex(0);

                //在元素上结束拖拽时
//                int finalRow = k + 1;
//                int finalCol = i + 1;
//                slot.addEventListener(UIEvents.DRAG_PERFORM, e -> moveItem(finalCol, finalRow, e, inventory));
                slotRefs[i][k] = slot;
                // 添加到网格中（自动按顺序填充）
                addChild(slot);
            }
        }
        //打开UI时读取玩家身上的物品
        updateUIBagWithTrueBag(inventory,dragSession);
        //将自身进行存储
        dragSession.gridBag = this;
    }

    ///打开UI时读取玩家背包物品
    private void updateUIBagWithTrueBag(Inventory inventory, DragSession dragSession){
        //打开UI时读取玩家身上的物品
        for (int a = 0; a < inventory.getContainerSize(); a++) {
            ItemStack stack = inventory.getItem(a).copy();
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
                var gridItem = new GridItem(dragSession, stack);
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

                float w = dragSession.itemWidth;
                float h = dragSession.itemHeight;

                setOffset(dragSession,gridItem);

                gridItem.getLayout().width(w).height(h);

                addChild(gridItem);
            }
        }
    }

    ///物品移动
    private void moveItem(int col, int row, UIEvent e, Inventory inventory){
        //如果该格已经被占有，则返回
        if (bagGrids[col-1][row-1] != null) return;

        var source = e.dragHandler.dragSource;
//        var target = e.target;
        if (source == null) return;
//        if (source == target) return;

        addChildren(source);

        //设置网格相关属性
        if (source instanceof GridItem gridItem) {
            //撤销老占用
            if (gridItem.getParent() instanceof GridBag parent) {
                if (gridItem.slotColumn - 1 >= 0 && gridItem.slotRow - 1 >= 0) {
                    parent.bagGrids[gridItem.slotColumn - 1][gridItem.slotRow - 1] = null;
                }
            }

            //设置新占用
            gridItem.slotRow = row;
            gridItem.slotColumn = col;
            bagGrids[col - 1][row - 1] = gridItem;

            //设置显示时的网格位置
            gridItem.layout(layout -> layout
                    .gridColumn("" + (col))
                    .gridRow("" + (row))
            );

            //设置偏移，使图标相对于格子居中
            if(e.dragHandler.draggingObject instanceof DragSession dragSession) {
                setOffset(dragSession, gridItem);
                dragSession.success = true;
            }
            //为玩家添加物品
            CompoundTag nbt = null;
            if (gridItem.itemStackCopy != null) {
                CustomData customData = gridItem.itemStackCopy.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    nbt = customData.copyTag();
                }
            }

            //在玩家的原版背包中设置物品，若无则添加，若有则修改
            if (nbt == null || !nbt.contains("slotNumber")) {
                // 遍历主背包，在第一个空位添加复制物品给玩家
                gridItem.itemStackCopy = gridItem.itemStack.copy();
                CustomData itemData = gridItem.itemStackCopy.get(DataComponents.CUSTOM_DATA);
                nbt = itemData != null ? itemData.copyTag() : new CompoundTag();

                for (int a = 0; a < inventory.getContainerSize(); a++) {
                    ItemStack stack = inventory.getItem(a);
                    if (stack.isEmpty()) {
                        gridItem.slotNumber = a;
                        nbt.putInt("column", col);
                        nbt.putInt("row", row);
                        nbt.putInt("slotNumber", a);
                        gridItem.itemStackCopy.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                        inventory.setItem(a, gridItem.itemStackCopy);
                        break;
                    }
                }
            } else {
                var slotNumber = nbt.getInt("slotNumber");
                CustomData itemData = gridItem.itemStackCopy.get(DataComponents.CUSTOM_DATA);
                nbt = itemData != null ? itemData.copyTag() : new CompoundTag();

                nbt.putInt("column", col);
                nbt.putInt("row", row);
                nbt.putInt("slotNumber", slotNumber);

                gridItem.itemStackCopy.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                inventory.setItem(slotNumber, gridItem.itemStackCopy);
            }
        }
    }

    ///创建格子并设置基础信息
    private UIElement createSlot(int k, int i,String path){
        UIElement slot = new UIElement();
        int finalRow = k + 1;
        int finalCol = i + 1;
        slot.layout(layout -> layout
                .width(Config.GRID_SLOT_WIDTH.get().floatValue())
                .height(Config.GRID_SLOT_HEIGHT.get().floatValue())
                .gridColumn("" + (finalCol))
                .gridRow("" + (finalRow))
        );
        slot.style(style -> style.background(
                        SpriteTexture.of(path))
                .zIndex(GridZIndex)
        );
        return slot;
    }

    ///完成移动，设置偏移并将此次移动设为成功
    private void setOffset(DragSession data, UIElement uiElement){
//        float w = data.itemWidth;
//        float h = data.itemHeight;
//        float offsetX = -(w - Config.GRID_SLOT_WIDTH.get().floatValue()) / 2f;
//        float offsetY = -(h - Config.GRID_SLOT_HEIGHT.get().floatValue()) / 2f;
//        uiElement.transform(transform -> transform.translate(offsetX, offsetY));
//            uiElement.getLayout().width(w).height(h);
    }

    ///根据鼠标位置尝试放置物品
    public void tryPlaceItemAt(GridItem item, float mouseX, float mouseY, UIEvent e) {
        Inventory inventory = player.getInventory();
        for (int c = 0; c < column; c++) {
            for (int r = 0; r < row; r++) {
                // 检查是否已被占用
                if (bagGrids[c][r] != null) continue;

                UIElement slot = slotRefs[c][r];
                if (slot == null) continue;

                float sx = slot.getPositionX();
                float sy = slot.getPositionY();
                float sw = slot.getSizeWidth();
                float sh = slot.getSizeHeight();

                var itemWidth = ItemScaleConfig.getInstance().get(item.itemStack).width();
                var itemHeight = ItemScaleConfig.getInstance().get(item.itemStack).height();


                var anchorOffsetX = sw/2 - sw/2 * itemWidth;
                var anchorOffsetY = sh/2 - sh/2 * itemHeight;

                float anchorX = mouseX + anchorOffsetX;
                float anchorY = mouseY + anchorOffsetY;


                // 检查鼠标是否在格子内
                if (anchorX >= sx && anchorX < sx + sw &&
                        anchorY >= sy && anchorY < sy + sh) {

                    // 执行放置
                    moveItem(c+1, r+1, e, inventory);
                }
            }
        }
    }
}
