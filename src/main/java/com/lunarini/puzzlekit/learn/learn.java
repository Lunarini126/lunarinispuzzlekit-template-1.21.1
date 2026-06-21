package com.lunarini.puzzlekit.learn;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lunarini.puzzlekit.blockEntity.TestBlockEntity;
import com.lunarini.puzzlekit.gui.session.DragSession;
import com.lunarini.puzzlekit.gui.uiElement.FillSlot;
import com.lunarini.puzzlekit.gui.uiElement.GridBag;
import com.lunarini.puzzlekit.gui.uiElement.GridItem;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class learn {

    public static ModularUI createModularUI(TestBlockEntity be, Player player) {
        //字段
        var dragSession = new DragSession();
        dragSession.player = player;


        var root = new UIElement();
        root.layout(layout -> layout
                        .width(220)
                        .height(220)
                        .display(TaffyDisplay.FLEX)
                        .flexDirection(FlexDirection.ROW)
                        .justifyContent(AlignContent.CENTER)   // 主轴居中 → 垂直居中
                        .alignItems(AlignItems.CENTER)
                )
                .style(style -> style.background(Sprites.BORDER)
        );
        var gird = new GridBag("lunarinispuzzlekit:textures/grid.png",dragSession);

        root.addChildren(gird);

        // 基础物品
        Item gunItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse("tacz:modern_kinetic_gun"));
        ItemStack gunStack = new ItemStack(gunItem);

// 设置 GunId NBT（关键！）
        CompoundTag nbt = new CompoundTag();
        nbt.putString("GunId", "tacz:ak47");  // 枪械型号，格式：命名空间:枪名

// 可选：设置射击模式
        nbt.putString("GunFireMode", "AUTO");  // AUTO / SEMI / BURST

// 可选：设置配件
        CompoundTag attachmentTag = new CompoundTag();
        attachmentTag.putString("muzzle", "tacz:silencer");  // 枪口
        attachmentTag.putString("scope", "tacz:scope_4x");   // 瞄准镜
        nbt.put("Attachment", attachmentTag);

// 可选：设置当前弹药数
        nbt.putInt("CurrentAmmoCount", 30);

// 写入物品栈
        gunStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        var fillSlot = new FillSlot("lunarinispuzzlekit:textures/grid.png",dragSession,new GridItem(dragSession,gunStack));
        var fillSlot1 = new FillSlot("lunarinispuzzlekit:textures/grid.png",dragSession,new GridItem(dragSession,new ItemStack(Items.GOLD_BLOCK)));
        var fillSlot2 = new FillSlot("lunarinispuzzlekit:textures/grid.png",dragSession,new GridItem(dragSession,new ItemStack(Items.APPLE)));
        root.addChildren(fillSlot);
        root.addChildren(fillSlot1);
        root.addChildren(fillSlot2);

        var ui = UI.of(root);
        var modularUI = ModularUI.of(ui);

        gird.addEventListener(UIEvents.MOUSE_DOWN,e -> {
            if (e.button != 1) return;
            var gridItemList = modularUI.getElementsByType(GridItem.class);
            for (GridItem item : gridItemList){
                LDLib2.LOGGER.info("名称："+ item.itemStack.getDisplayName().getString() + "列：" + item.slotColumn + "行" + item.slotRow);
            }
        });
        return modularUI;


//        //逻辑相关变量
//        AtomicReference<UIElement> currentSourceSlot = new AtomicReference<>();
//        AtomicReference<Boolean> moveSuccess = new AtomicReference<>(false);
//
//        var root = new UIElement();
//        var grid = new UIElement().layout(layout -> layout.display(TaffyDisplay.GRID)).style(style -> style.background(Sprites.BORDER));
//        root.layout(layout -> layout
//                        .width(220)
//                        .height(220)
//                        .display(TaffyDisplay.FLEX)
//                        .flexDirection(FlexDirection.ROW)
//                        .justifyContent(AlignContent.CENTER)   // 主轴居中 → 垂直居中
//                        .alignItems(AlignItems.CENTER)
//                )
//                .style(style -> style.background(Sprites.BORDER)
//        );
//
//        //右侧道具存放栏
//        var panelRight = new UIElement()
//                .layout(layout -> layout
//                        .width(80)
//                        .height(80)
//                        .display(TaffyDisplay.GRID)
//                        .gridTemplateColumns("repeat(2, 40px)")
//                        .gridTemplateRows("repeat(3, 40px)")
//                )
//                .style(style -> style.background(Sprites.BORDER));
//        //可拖拽元素
//        var sourceSlot = new UIElement()
//                .layout(layout -> layout.width(40).height(40))
//                .style(style -> style.background(Sprites.BORDER));
//        var image = new UIElement()
//                .layout(layout -> layout.width(30).height(30)).transform(transform -> transform.translate(5,5))
//                .style(style -> style
//                        .background(SpriteTexture.of("lunarinispuzzlekit:textures/t_energy_block.png"))
//                        .zIndex(200)
//                );
//        int imageW = 60;
//        int imageH = 60;
//        image.addEventListener(UIEvents.MOUSE_DOWN, e -> {
//            // 当鼠标按下时开始拖拽
//            moveSuccess.set(false);
//            image.startDrag(new DragPayload(image,imageW,imageH), SpriteTexture.of("lunarinispuzzlekit:textures/t_energy_block.png"));
//            currentSourceSlot.set(image.getParent());
//            image.getParent().removeChild(image);
//        });
//        image.addEventListener(UIEvents.DRAG_END,e -> {
//            Minecraft.getInstance().execute(() -> {
//                if (!moveSuccess.get()){
//                    currentSourceSlot.get().addChildren(e.dragHandler.dragSource);
//                }
//            });
//        });
//        sourceSlot.addChildren(image);
//        sourceSlot.addEventListener(UIEvents.DRAG_PERFORM, e -> {
//            if (e.dragHandler.dragSource== e.target){
//                LDLib2.LOGGER.info("给到自己了");
//                return;
//            }
//            var source = e.dragHandler.dragSource;
//            source.getLayout().height(30).width(30);
//            source.transform(transform -> transform.translate(5, 5));
//            e.target.addChildren(source);
//            moveSuccess.set(true);
//        });
//
//        grid.layout(layout -> layout
//                .display(TaffyDisplay.GRID)          // 使用网格布局
//                .gridTemplateColumns("repeat(6, 20px)")
//                .gridTemplateRows("repeat(6, 20px)")
////                .gapAll(2)
//                );
//
//        // 3. 添加图片格子（9x4 = 36个）
//        for (int row = 1; row < 5; row++) {
//            for (int col = 1; col < 5; col++) {
//                UIElement slot = new UIElement();
////                 设置大小（与网格单元格大小一致）
//                int finalRow = row;
//                int finalCol = col;
//                slot.layout(layout -> layout
//                        .width(20)
//                        .height(20)
//                        .gridColumn("" + (finalCol+1))
//                        .gridRow("" + (finalRow+1))
//                );
//                // 设置背景图片（使用你提供的 SpriteTexture）
//                slot.style(style -> style.background(
//                        SpriteTexture.of("lunarinispuzzlekit:textures/grid.png"))
//                        .zIndex(1)
//                );
//                //在元素上结束拖拽时
//                slot.addEventListener(UIEvents.DRAG_PERFORM, e -> {
//                    if (e.dragHandler.dragSource== e.target){
//                        LDLib2.LOGGER.info("给到自己了");
//                        return;
//                    }
////                    e.target.addChildren(e.dragHandler.dragSource);
//                    e.dragHandler.dragSource.layout(layout -> layout
//                            .gridColumn("" + (finalCol+1))
//                            .gridRow("" + (finalRow+1))
//                    );
//                    var source = e.dragHandler.dragSource;
//                    grid.addChildren(e.dragHandler.dragSource);
//                    Minecraft.getInstance().execute(() -> {
//                        if (e.dragHandler.draggingObject instanceof DragPayload data){
//                            float w = data.width;
//                            float h = data.height;
//                            // 格子大小为 20x20，假设图片大于格子，需要偏移使中心对齐
//                            float offsetX = -(w - 20) / 2f;
//                            float offsetY = -(h - 20) / 2f;
//                            source.transform(transform -> transform.translate(offsetX, offsetY));
//                            source.getLayout().width(w).height(h);
//                        }
//                    });
//                    moveSuccess.set(true);
//                });
//                // 添加到网格中（自动按顺序填充）
//                grid.addChild(slot);
//            }
//        }
//
//        for (int row = 0; row < 5; row++) {
//            for (int col = 0; col < 5; col++) {
//                boolean isEdge = (row == 0 || row == 5 || col == 0 || col == 5);
//                boolean isCorner = (row == 0 && col == 0) || (row == 0 && col == 5) ||
//                        (row == 5 && col == 0) || (row == 5 && col == 5);
//                if (!isEdge || isCorner) continue;
//
//                UIElement slot = new UIElement();
//                int finalCol = col;
//                int finalRow = row;
//                slot.layout(layout -> layout
//                        .width(20)
//                        .height(20)
//                        .gridColumn("" + (finalCol + 1))
//                        .gridRow("" + (finalRow + 1))
//                );
//                slot.style(style -> style.background(
//                        SpriteTexture.of("lunarinispuzzlekit:textures/grid_number.png")
//                ));
//
//                // 根据位置设置旋转角度（弧度）
//                float angle = 0;
//                if (row == 0) { // 上排
//                    angle = (float) -180 / 2;   // 顺时针90°
//                } else if (row == 5) { // 下排
//                    angle = (float) 180 / 2;  // 逆时针90°
//                } else if (col == 0) { // 左列
//                    angle = (float) 180;       // 180°
//                } // 右列保持0
//
//                if (angle != 0) {
//                    final float finalAngle = angle;
//                    slot.transform(transform -> transform.rotation(finalAngle));
//                }
//
//                grid.addChild(slot);
//            }
//        }
//
//        panelRight.addChildren(sourceSlot);
//        root.addChildren(grid,panelRight);
//
//        var ui = UI.of(root);
//        var modularUI = ModularUI.of(ui);
//        return modularUI;
    }
}