package com.lunarini.puzzlekit.init;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lunarini.puzzlekit.gui.session.DragSession;
import com.lunarini.puzzlekit.gui.uiElement.FillSlot;
import com.lunarini.puzzlekit.gui.uiElement.GridBag;
import com.lunarini.puzzlekit.gui.uiElement.GridItem;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lunarini.puzzlekit.learn.learn;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "lunarinispuzzlekit", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModKeys {

    public static final KeyMapping OPEN_PUZZLE_UI = new KeyMapping(
            "key.lunarinispuzzlekit.open_puzzle_ui",  // 翻译键
            GLFW.GLFW_KEY_B,                           // 默认按键：B
            "key.categories.lunarinispuzzlekit"        // 分类
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PUZZLE_UI);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // 检测按键是否刚被按下（consumeClick 会重置状态，防止重复触发）
        if (OPEN_PUZZLE_UI.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            // 如果当前已经有屏幕打开，先关闭它（可选）
            if (mc.screen != null) {
                mc.setScreen(null);
                return;
            }

            // 创建并打开 UI
            var modularUI = learn.createModularUI(null,mc.player);
            mc.setScreen(new ModularUIScreen(modularUI, Component.empty()));
        }
    }
}
